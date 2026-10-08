import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.brotli.dec.BrotliInputStream;

/**
 * Minimal WOFF2 -> TTF converter (build tool, not part of the app).
 * Handles Brotli, transformed glyf/loca and transformed hmtx. Variable-font tables are copied untouched.
 * Usage: java -cp dec-0.1.2.jar tools/Woff2ToTtf.java in.woff2 out.ttf
 */
public class Woff2ToTtf {
    static final String[] KNOWN = {
        "cmap", "head", "hhea", "hmtx", "maxp", "name", "OS/2", "post", "cvt ", "fpgm", "glyf", "loca", "prep",
        "CFF ", "VORG", "EBDT", "EBLC", "gasp", "hdmx", "kern", "LTSH", "PCLT", "VDMX", "vhea", "vmtx", "BASE",
        "GDEF", "GPOS", "GSUB", "EBSC", "JSTF", "MATH", "CBDT", "CBLC", "COLR", "CPAL", "SVG ", "sbix", "acnt",
        "avar", "bdat", "bloc", "bsln", "cvar", "fdsc", "feat", "fmtx", "fvar", "gvar", "hsty", "just", "lcar",
        "mort", "morx", "opbd", "prop", "trak", "Zapf", "Silf", "Glat", "Gloc", "Feat", "Sill"
    };

    static class Table {
        String tag; int flags; int origLength; int transformLength; boolean transformed; byte[] data;
    }

    // ---- simple big-endian reader ----
    static class In {
        final byte[] b; int p;
        In(byte[] b, int p) { this.b = b; this.p = p; }
        int u8() { return b[p++] & 0xff; }
        int u16() { int v = ((b[p] & 0xff) << 8) | (b[p + 1] & 0xff); p += 2; return v; }
        int s16() { return (short) u16(); }
        long u32() { long v = ((long) (b[p] & 0xff) << 24) | ((b[p + 1] & 0xff) << 16) | ((b[p + 2] & 0xff) << 8) | (b[p + 3] & 0xff); p += 4; return v; }
        int base128() {
            int v = 0;
            for (int i = 0; i < 5; i++) {
                int c = u8();
                v = (v << 7) | (c & 0x7f);
                if ((c & 0x80) == 0) return v;
            }
            throw new IllegalStateException("bad UIntBase128");
        }
        int u255() {
            int c = u8();
            if (c == 253) return u16();
            if (c == 255) return 253 + u8();
            if (c == 254) return 506 + u8();
            return c;
        }
    }

    static class Out {
        byte[] b = new byte[1024]; int n;
        void ensure(int k) { if (n + k > b.length) b = Arrays.copyOf(b, Math.max(b.length * 2, n + k)); }
        void u8(int v) { ensure(1); b[n++] = (byte) v; }
        void u16(int v) { u8(v >> 8); u8(v); }
        void u32(long v) { u16((int) (v >> 16)); u16((int) v); }
        void bytes(byte[] s, int off, int len) { ensure(len); System.arraycopy(s, off, b, n, len); n += len; }
        void pad4() { while ((n & 3) != 0) u8(0); }
        byte[] toArray() { return Arrays.copyOf(b, n); }
    }

    public static void main(String[] args) throws Exception {
        byte[] f = Files.readAllBytes(Paths.get(args[0]));
        In in = new In(f, 0);
        if (in.u32() != 0x774F4632L) throw new IllegalStateException("not WOFF2");
        long flavor = in.u32();
        in.u32(); // length
        int numTables = in.u16();
        in.u16();
        in.u32(); // totalSfntSize
        long compressedSize = in.u32();
        in.p += 2 + 2 + 4 * 5; // versions, meta, private

        List<Table> tables = new ArrayList<>();
        for (int i = 0; i < numTables; i++) {
            Table t = new Table();
            t.flags = in.u8();
            int idx = t.flags & 0x3f;
            if (idx == 63) { t.tag = new String(f, in.p, 4, "ISO-8859-1"); in.p += 4; } else t.tag = KNOWN[idx];
            int version = (t.flags >> 6) & 3;
            boolean glyfLoca = t.tag.equals("glyf") || t.tag.equals("loca");
            t.transformed = glyfLoca ? version == 0 : version != 0;
            t.origLength = in.base128();
            t.transformLength = t.origLength;
            if (t.transformed) t.transformLength = in.base128();
            tables.add(t);
        }
        byte[] comp = Arrays.copyOfRange(f, in.p, in.p + (int) compressedSize);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (BrotliInputStream bis = new BrotliInputStream(new ByteArrayInputStream(comp))) {
            byte[] buf = new byte[65536]; int r;
            while ((r = bis.read(buf)) > 0) bos.write(buf, 0, r);
        }
        byte[] raw = bos.toByteArray();
        int off = 0;
        Map<String, Table> map = new LinkedHashMap<>();
        for (Table t : tables) {
            int len = t.transformed ? t.transformLength : t.origLength;
            t.data = Arrays.copyOfRange(raw, off, off + len);
            off += len;
            map.put(t.tag, t);
        }

        Table head = map.get("head");
        Table maxp = map.get("maxp");
        Table hhea = map.get("hhea");
        int numGlyphs = new In(maxp.data, 4).u16();
        int[] xMins = new int[numGlyphs];

        if (map.get("glyf").transformed) {
            byte[][] res = rebuildGlyf(map.get("glyf").data, numGlyphs, xMins);
            map.get("glyf").data = res[0];
            map.get("loca").data = res[1];
            map.get("loca").transformed = false;
            map.get("glyf").transformed = false;
            head.data[50] = 0; head.data[51] = 1; // indexToLocFormat = long
        }
        Table hmtx = map.get("hmtx");
        if (hmtx != null && hmtx.transformed) {
            int numHMetrics = new In(hhea.data, 34).u16();
            hmtx.data = rebuildHmtx(hmtx.data, numGlyphs, numHMetrics, xMins);
            hmtx.transformed = false;
        }

        // ---- write sfnt ----
        List<Table> sorted = new ArrayList<>(map.values());
        sorted.sort(Comparator.comparing(t -> t.tag));
        int n = sorted.size();
        int entrySelector = 31 - Integer.numberOfLeadingZeros(n);
        int searchRange = (1 << entrySelector) * 16;
        Out o = new Out();
        o.u32(flavor); o.u16(n); o.u16(searchRange); o.u16(entrySelector); o.u16(n * 16 - searchRange);
        int dataOff = 12 + 16 * n;
        // head checkSumAdjustment zeroed first
        for (int i = 8; i < 12; i++) head.data[i] = 0;
        int cur = dataOff;
        int[] offsets = new int[n];
        for (int i = 0; i < n; i++) {
            Table t = sorted.get(i);
            offsets[i] = cur;
            o.bytes(t.tag.getBytes("ISO-8859-1"), 0, 4);
            o.u32(checksum(t.data));
            o.u32(cur);
            o.u32(t.data.length);
            cur += (t.data.length + 3) & ~3;
        }
        for (Table t : sorted) { o.bytes(t.data, 0, t.data.length); o.pad4(); }
        byte[] font = o.toArray();
        long total = checksum(font);
        long adj = (0xB1B0AFBAL - total) & 0xffffffffL;
        for (int i = 0; i < n; i++) {
            if (sorted.get(i).tag.equals("head")) {
                int p = offsets[i] + 8;
                font[p] = (byte) (adj >> 24); font[p + 1] = (byte) (adj >> 16); font[p + 2] = (byte) (adj >> 8); font[p + 3] = (byte) adj;
            }
        }
        Files.write(Paths.get(args[1]), font);
        System.out.println("wrote " + args[1] + " tables=" + n + " glyphs=" + numGlyphs + " bytes=" + font.length);
    }

    static long checksum(byte[] d) {
        long sum = 0;
        for (int i = 0; i < d.length; i += 4) {
            long v = 0;
            for (int k = 0; k < 4; k++) v = (v << 8) | (i + k < d.length ? d[i + k] & 0xff : 0);
            sum = (sum + v) & 0xffffffffL;
        }
        return sum;
    }

    static int withSign(int flag, int v) { return (flag & 1) != 0 ? v : -v; }

    static byte[][] rebuildGlyf(byte[] d, int numGlyphs, int[] xMins) {
        In h = new In(d, 0);
        h.u16();
        int optionFlags = h.u16();
        int ng = h.u16();
        h.u16(); // indexFormat
        int sNContour = (int) h.u32(), sNPoints = (int) h.u32(), sFlag = (int) h.u32(), sGlyph = (int) h.u32();
        int sComposite = (int) h.u32(), sBbox = (int) h.u32(), sInstr = (int) h.u32();
        if (ng != numGlyphs) throw new IllegalStateException("glyph count mismatch");
        int p = h.p;
        In nContour = new In(d, p); p += sNContour;
        In nPoints = new In(d, p); p += sNPoints;
        In flagS = new In(d, p); p += sFlag;
        In glyphS = new In(d, p); p += sGlyph;
        In compS = new In(d, p); p += sComposite;
        int bboxStart = p;
        int bitmapLen = ((numGlyphs + 31) >> 5) << 2;
        In bboxS = new In(d, bboxStart + bitmapLen); p += sBbox;
        In instrS = new In(d, p); p += sInstr;
        byte[] overlap = null;
        if ((optionFlags & 1) != 0) overlap = Arrays.copyOfRange(d, p, p + ((numGlyphs + 7) >> 3));

        Out glyf = new Out();
        int[] loca = new int[numGlyphs + 1];
        for (int g = 0; g < numGlyphs; g++) {
            loca[g] = glyf.n;
            int nc = nContour.s16();
            boolean hasBbox = (d[bboxStart + (g >> 3)] & (0x80 >> (g & 7))) != 0;
            if (nc == 0) { xMins[g] = 0; continue; }
            if (nc > 0) {
                int[] endPts = new int[nc];
                int total = 0;
                for (int c = 0; c < nc; c++) { total += nPoints.u255(); endPts[c] = total - 1; }
                int[] xs = new int[total], ys = new int[total]; boolean[] on = new boolean[total];
                int[] flags = new int[total];
                for (int i = 0; i < total; i++) flags[i] = flagS.u8();
                int x = 0, y = 0;
                for (int i = 0; i < total; i++) {
                    int flag = flags[i];
                    on[i] = (flag >> 7) == 0;
                    flag &= 0x7f;
                    int dx, dy;
                    if (flag < 10) { dx = 0; dy = withSign(flag, ((flag & 14) << 7) + glyphS.u8()); }
                    else if (flag < 20) { dx = withSign(flag, (((flag - 10) & 14) << 7) + glyphS.u8()); dy = 0; }
                    else if (flag < 84) {
                        int b0 = flag - 20, b1 = glyphS.u8();
                        dx = withSign(flag, 1 + (b0 & 0x30) + (b1 >> 4));
                        dy = withSign(flag >> 1, 1 + ((b0 & 0x0c) << 2) + (b1 & 0x0f));
                    } else if (flag < 120) {
                        int b0 = flag - 84, a = glyphS.u8(), b = glyphS.u8();
                        dx = withSign(flag, 1 + ((b0 / 12) << 8) + a);
                        dy = withSign(flag >> 1, 1 + (((b0 % 12) >> 2) << 8) + b);
                    } else if (flag < 124) {
                        int a = glyphS.u8(), b2 = glyphS.u8(), c2 = glyphS.u8();
                        dx = withSign(flag, (a << 4) + (b2 >> 4));
                        dy = withSign(flag >> 1, ((b2 & 0x0f) << 8) + c2);
                    } else {
                        int a = glyphS.u8(), b = glyphS.u8(), c2 = glyphS.u8(), e = glyphS.u8();
                        dx = withSign(flag, (a << 8) + b);
                        dy = withSign(flag >> 1, (c2 << 8) + e);
                    }
                    x += dx; y += dy; xs[i] = x; ys[i] = y;
                }
                int instrLen = glyphS.u255();
                byte[] instr = Arrays.copyOfRange(instrS.b, instrS.p, instrS.p + instrLen);
                instrS.p += instrLen;
                int xMin, yMin, xMax, yMax;
                if (hasBbox) { xMin = bboxS.s16(); yMin = bboxS.s16(); xMax = bboxS.s16(); yMax = bboxS.s16(); }
                else {
                    xMin = yMin = Integer.MAX_VALUE; xMax = yMax = Integer.MIN_VALUE;
                    for (int i = 0; i < total; i++) { xMin = Math.min(xMin, xs[i]); xMax = Math.max(xMax, xs[i]); yMin = Math.min(yMin, ys[i]); yMax = Math.max(yMax, ys[i]); }
                }
                xMins[g] = xMin;
                glyf.u16(nc); glyf.u16(xMin); glyf.u16(yMin); glyf.u16(xMax); glyf.u16(yMax);
                for (int c = 0; c < nc; c++) glyf.u16(endPts[c]);
                glyf.u16(instrLen); glyf.bytes(instr, 0, instrLen);
                boolean ov = overlap != null && (overlap[g >> 3] & (0x80 >> (g & 7))) != 0;
                for (int i = 0; i < total; i++) glyf.u8((on[i] ? 1 : 0) | (i == 0 && ov ? 0x40 : 0));
                int px = 0;
                for (int i = 0; i < total; i++) { glyf.u16(xs[i] - px); px = xs[i]; }
                int py = 0;
                for (int i = 0; i < total; i++) { glyf.u16(ys[i] - py); py = ys[i]; }
            } else {
                int start = compS.p; boolean instrs = false; int flags;
                do {
                    flags = compS.u16(); compS.u16();
                    compS.p += (flags & 1) != 0 ? 4 : 2;
                    if ((flags & 0x08) != 0) compS.p += 2;
                    else if ((flags & 0x40) != 0) compS.p += 4;
                    else if ((flags & 0x80) != 0) compS.p += 8;
                    if ((flags & 0x100) != 0) instrs = true;
                } while ((flags & 0x20) != 0);
                int len = compS.p - start;
                int xMin = bboxS.s16(), yMin = bboxS.s16(), xMax = bboxS.s16(), yMax = bboxS.s16();
                xMins[g] = xMin;
                glyf.u16(0xFFFF); glyf.u16(xMin); glyf.u16(yMin); glyf.u16(xMax); glyf.u16(yMax);
                glyf.bytes(compS.b, start, len);
                if (instrs) {
                    int il = glyphS.u255();
                    glyf.u16(il); glyf.bytes(instrS.b, instrS.p, il); instrS.p += il;
                }
            }
            glyf.pad4();
        }
        loca[numGlyphs] = glyf.n;
        Out lo = new Out();
        for (int v : loca) lo.u32(v);
        return new byte[][] { glyf.toArray(), lo.toArray() };
    }

    static byte[] rebuildHmtx(byte[] d, int numGlyphs, int numHMetrics, int[] xMins) {
        In in = new In(d, 0);
        int flags = in.u8();
        boolean lsbOmitted = (flags & 1) != 0, leftOmitted = (flags & 2) != 0;
        int[] adv = new int[numHMetrics];
        for (int i = 0; i < numHMetrics; i++) adv[i] = in.u16();
        int[] lsb = new int[numGlyphs];
        for (int i = 0; i < numHMetrics; i++) lsb[i] = lsbOmitted ? xMins[i] : in.s16();
        for (int i = numHMetrics; i < numGlyphs; i++) lsb[i] = leftOmitted ? xMins[i] : in.s16();
        Out o = new Out();
        for (int i = 0; i < numGlyphs; i++) {
            if (i < numHMetrics) o.u16(adv[i]);
            o.u16(lsb[i]);
        }
        return o.toArray();
    }
}
