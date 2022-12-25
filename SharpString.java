import java.nio.charset.StandardCharsets;

class SharpString {
    static int toAsciiCode(char ch) {
        byte[] bytes = String.valueOf(ch).getBytes(StandardCharsets.US_ASCII);
        return bytes[0];
    }
    static String toSharp(String s) {
        String percent = "";
        for (int n = 0;n != s.length();n++) {
            if (
                (
                    48 <= toAsciiCode(s.charAt(n)) &&
                    57 >= toAsciiCode(s.charAt(n))
                ) ||
                (
                    65 <= toAsciiCode(s.charAt(n)) &&
                    90 >= toAsciiCode(s.charAt(n)) 
                ) ||
                (
                    97 <= toAsciiCode(s.charAt(n)) &&
                    122 >= toAsciiCode(s.charAt(n)) 
                )
            ) {
                percent += s.charAt(n);
            }else {
                percent += "_" + convertToUnicode(s.charAt(n)) + "_";
            }
        }
        return percent;
    }
    static String toString(String ps) {
        if (ps.isEmpty()) {
            return "";
        }
        String s = "";
        boolean loop = true;
        int n = 0;
        while (loop) {
            if (ps.charAt(n) == '_') {
                String tmp = "";
                for (int i = 1;i != ps.length() - n;i++) {
                    if (ps.charAt(n + i) == '_') {
                        n += i;
                        break;
                    }else {
                        tmp += ps.charAt(n + i);
                    }
                }
                s += String.valueOf(convertToOiginal(Integer.parseInt(tmp)));
            }else {
                s += String.valueOf(ps.charAt(n));
            }

            n++;

            if (ps.length() <= n) {
                break;
            }
        }
        return s;
    }

    static int convertToUnicode(char original) {
        return Character.codePointAt(String.valueOf(original), 0);
    }
    static char convertToOiginal(int unicode) {  
        int[] unichar = {
            unicode
        };
        return new String(unichar, 0, 1).charAt(0);
    }
}