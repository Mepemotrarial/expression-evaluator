package com.meppy.expression;

public final class Expressions {
    public static final String FORMATTING_CULTURE = "[(1 + fact(value)) / 3 @ \"#.00\" : \"bg_BG\"]";
    public static final String INVALID_CULTURE_1 = "[10.5 @ \"#\" : bg_BG\"]";
    public static final String INVALID_CULTURE_2 = "[10.5 @ \"#\" : \"bg_BG]";
    public static final String SEPARATOR = "[1 + 2; 3 / 4]";
    public static final String POWER = "[2 ^ 3]";
    public static final String DOT = "[agg.avg]";
    public static final String STRING_E = "[\"test\" + 102E-1]";
    public static final String NULL = "[x != null]";
    public static final String COLOR = "[#ABC123]";
    public static final String INVALID_1 = "[$asd]";
    public static final String INVALID_2 = "[1 / ]";
    public static final String INVALID_3 = "[1 + 2";
    public static final String INVALID_4 = "[1.5";
    public static final String INVALID_5 = "[1.5px";
    public static final String INVALID_6 = "[+]";
    public static final String INVALID_7 = "This is an invalid [1+2 null";
    public static final String INVALID_8 = "[true || +]";
    public static final String INVALID_9 = "[false && -]";
    public static final String INVALID_10 = "[1 | *]";
    public static final String INVALID_11 = "[2 & /]";
    public static final String INVALID_12 = "[3.4 ^ -]";
    public static final String INVALID_13 = "[5 == -]";
    public static final String INVALID_14 = "[6 < ^]";
    public static final String INVALID_15 = "[7 + +]";
    public static final String INVALID_16 = "[7 * /]";
    public static final String INVALID_17 = "[test.*]";
    public static final String INVALID_18 = "[!*]";
    public static final String INVALID_19 = "[-+]";
    public static final String INVALID_20 = "[(1+2]";
    public static final String INVALID_21 = "[(-]";
    public static final String INVALID_22 = "[func(1]";
    public static final String INVALID_23 = "[(1 + 2) 3]";
    public static final String INVALID_24 = "[func(1 + )]";
    public static final String INVALID_INCOMPLETE_FORMATTING = "[10.5 @ \"#]";
    public static final String INVALID_FORMATTING = "[10.5 @ ?";
    public static final String INVALID_CULTURE_3 = "[10.5 @ \"#\" : ?]";
    public static final String INVALID_CULTURE_4 = "[10.5 @ \"#\" : ?";
    public static final String INVALID_INCOMPLETE_STRING = "[\"test";
    public static final String INVALID_INCOMPLETE_STRING_2 = "[?";
    public static final String EMBEDDED = "[1] + [2] = [1 + 2]";
    public static final String COMPARISON = "[1 > 2 && 2 >= 3 || 3 < 4 || 5 <= 6 && 7 == 8 || 8 != 9]";
    public static final String IDENTIFIERS_NOT = "[a != !b]";

    public static final String FUNCTIONS_NOW = "[now()]";
    public static final String FUNCTIONS_E = "[e()]";
    public static final String FUNCTIONS_PI = "[pi()]";
    public static final String FUNCTIONS_TODAY = "[today()]";
    public static final String FUNCTIONS_ABS = "[abs(-1.2)]";
    public static final String FUNCTIONS_ASC = "[asc(\"test\")]";
    public static final String FUNCTIONS_ASC_NULL = "[asc(null)]";
    public static final String FUNCTIONS_ATN = "[atn(0.5)]";
    public static final String FUNCTIONS_CHR = "[chr(100)]";
    public static final String FUNCTIONS_CBOOL1 = "[cbool(1)]";
    public static final String FUNCTIONS_CBOOL2 = "[cbool(\"0\")]";
    public static final String FUNCTIONS_CBOOL3 = "[cbool(\"true\")]";
    public static final String FUNCTIONS_CDATE1 = "[cdate(2000000000)]";
    public static final String FUNCTIONS_CDATE3 = "[cdate(now())]";
    public static final String FUNCTIONS_CDATE_INVALID = "[cdate(\"abc-def\")]";
    public static final String FUNCTIONS_CDBL1 = "[cdbl(\"25.1E2\")]";
    public static final String FUNCTIONS_CDBL2 = "[cdbl(now())]";
    public static final String FUNCTIONS_CINT1 = "[cint(\"2.1\")]";
    public static final String FUNCTIONS_CINT2 = "[cint(now())]";
    public static final String FUNCTIONS_CLONG1 = "[clong(\"4294967296\")]";
    public static final String FUNCTIONS_CLONG2 = "[clong(now())]";
    public static final String FUNCTIONS_CSNG1 = "[csng(\"-5.5\")]";
    public static final String FUNCTIONS_CSNG2 = "[csng(now())]";
    public static final String FUNCTIONS_CSTR1 = "[cstr(-5.5)]";
    public static final String FUNCTIONS_CSTR2 = "[cstr(float(-5))]";
    public static final String FUNCTIONS_CSTR3 = "[cstr(now())]";
    public static final String FUNCTIONS_COS_ACOS = "[acos(cos(pi() / 3))]";
    public static final String FUNCTIONS_EXP = "[exp(10)]";
    public static final String FUNCTIONS_INT = "[int(3.1)]";
    public static final String FUNCTIONS_ISNULL = "[isNull(null)]";
    public static final String FUNCTIONS_ISNUMERIC = "[isNumeric(12.1E4)]";
    public static final String FUNCTIONS_ISNUMERIC_DATE = "[isNumeric(now())]";
    public static final String FUNCTIONS_ISNUMERIC_NULL = "[isNumeric(null)]";
    public static final String FUNCTIONS_LCASE = "[lcase(\"MiXeD\")]";
    public static final String FUNCTIONS_LEN = "[len(\"test string\")]";
    public static final String FUNCTIONS_LOG = "[log(10)]";
    public static final String FUNCTIONS_POW = "[pow(3, 2)]";
    public static final String FUNCTIONS_RND1 = "[rnd(-1)]";
    public static final String FUNCTIONS_RND2 = "[rnd(0)]";
    public static final String FUNCTIONS_RND3 = "[rnd(1)]";
    public static final String FUNCTIONS_SGN = "[sgn(-0.01)]";
    public static final String FUNCTIONS_SIN_ASIN = "[asin(sin(pi() / 2))]";
    public static final String FUNCTIONS_SPACE = "[space(4)]";
    public static final String FUNCTIONS_SQR_SQRT = "[sqr(3) / sqrt(3)]";
    public static final String FUNCTIONS_STR = "[str(1.234)]";
    public static final String FUNCTIONS_STRREVERSE = "[strReverse(\"was it a cat I saw\")]";
    public static final String FUNCTIONS_TAN = "[tan(pi() / 4)]";
    public static final String FUNCTIONS_TRIM = "[trim(space(10))]";
    public static final String FUNCTIONS_UCASE = "[ucase(\"mIxEd\")]";
    public static final String FUNCTIONS_TYPEOF = "[typeOf(10)]";
    public static final String FUNCTIONS_TYPEOF_NULL = "[typeOf(null)]";
    public static final String FUNCTIONS_ROUND = "[round(1.5)]";
    public static final String FUNCTIONS_INSTR = "[inStr(\"the good and the bad\", \"the\")]";
    public static final String FUNCTIONS_INSTR_NULL = "[inStr(null, null)]";
    public static final String FUNCTIONS_INSTRREV = "[inStrRev(\"the good and the bad\", \"the\")]";
    public static final String FUNCTIONS_INSTRREV_NULL = "[inStrRev(null, null)]";
    public static final String FUNCTIONS_LEFT = "[left(\"Contest\", 3) + left(\"junction\", 20) + left(null, 0)]";
    public static final String FUNCTIONS_RIGHT = "[right(\"Quo vadis\", 3) + right(\"junction\", 20) + right(null, 0)]";
    public static final String FUNCTIONS_STRCOMP = "[strComp(\"first\", \"second\")]";
    public static final String FUNCTIONS_STRING = "[string(5, 100)]";
    public static final String FUNCTIONS_MIN = "[min(-2, 1)]";
    public static final String FUNCTIONS_MAX = "[max(-2, 1)]";
    public static final String FUNCTIONS_IIF = "[iif(true, \"yes\", \"no\")]";
    public static final String FUNCTIONS_MID1 = "[mid(\"invisible\", 2, 4) + mid(\"invisible\", 6, 10)]";
    public static final String FUNCTIONS_MID2 = "[mid(\"small\", 10, 2)]";
    public static final String FUNCTIONS_REPLACE = "[replace(\"I was here\", \"was\", \"wasn't\")]";
    public static final String FUNCTIONS_REPLACE_NULL = "[replace(null, null, null)]";
    public static final String FUNCTIONS_INSUFFICIENT_PARAMS = "[iif(true)]";
    public static final String FUNCTIONS_EXCESS_PARAMS = "[min(3, 2, 1)]";
    public static final String FUNCTIONS_NOT_DEFINED = "[undefined()]";
    public static final String FUNCTIONS_INVALID_ARGUMENTS = "[pow(1, )]";

    public static final String OP_UNARY = "[!(-byte(1) + -short(2) + -3 + -long(4) + -float(5) + -0.6 > 0)]";

    public static final String OP_BYTE_BASIC = "[(byte(11) / byte(2)) + (byte(3) * byte(5)) + (byte(-9) + byte(1)) + (byte(4) - byte(14)) + (byte(13) % byte(5))]";
    public static final String OP_BYTE_BITWISE = "[(byte(11) & byte(2)) + (byte(2) ^ byte(5)) + (byte(5) | byte(19))]";
    public static final String OP_BYTE_COMPARISON = "[byte(1) > byte(2) && byte(2) >= byte(3) || byte(3) < byte(4) || byte(5) <= byte(6) && byte(7) == byte(8) || byte(8) != byte(9)]";
    public static final String OP_BYTE_INVALID = "[byte(1) && byte(2)]";

    public static final String OP_SHORT_BASIC = "[(short(11) / short(2)) + (short(3) * short(5)) + (short(-9) + short(1)) + (short(4) - short(14)) + (short(13) % short(5))]";
    public static final String OP_SHORT_BITWISE = "[(short(11) & short(2)) + (short(2) ^ short(5)) + (short(5) | short(19))]";
    public static final String OP_SHORT_COMPARISON = "[short(1) > short(2) && short(2) >= short(3) || short(3) < short(4) || short(5) <= short(6) && short(7) == short(8) || short(8) != short(9)]";
    public static final String OP_SHORT_INVALID = "[short(1) && short(2)]";

    public static final String OP_INTEGER_BASIC = "[(int(11) / int(2)) + (int(3) * int(5)) + (int(-9) + int(1)) + (int(4) - int(14)) + (int(13) % int(5))]";
    public static final String OP_INTEGER_BITWISE = "[(int(11) & int(2)) + (int(2) ^ int(5)) + (int(5) | int(19))]";
    public static final String OP_INTEGER_COMPARISON = "[int(1) > int(2) && int(2) >= int(3) || int(3) < int(4) || int(5) <= int(6) && int(7) == int(8) || int(8) != int(9)]";
    public static final String OP_INTEGER_INVALID = "[int(1) && int(2)]";

    public static final String OP_LONG_BASIC = "[(long(11) / long(2)) + (long(3) * long(5)) + (long(-9) + long(1)) + (long(4) - long(14)) + (long(13) % long(5))]";
    public static final String OP_LONG_BITWISE = "[(long(11) & long(2)) + (long(2) ^ long(5)) + (long(5) | long(19))]";
    public static final String OP_LONG_COMPARISON = "[long(1) > long(2) && long(2) >= long(3) || long(3) < long(4) || long(5) <= long(6) && long(7) == long(8) || long(8) != long(9)]";
    public static final String OP_LONG_INVALID = "[long(1) && long(2)]";

    public static final String OP_FLOAT_BASIC = "[(float(11) / float(2)) + (float(3) * float(5)) + (float(-9) + float(1)) + (float(4) - float(14)) + (float(13) % float(5))]";
    public static final String OP_FLOAT_POWER = "[float(2) ^ float(5)]";
    public static final String OP_FLOAT_COMPARISON = "[float(1) > float(2) && float(2) >= float(3) || float(3) < float(4) || float(5) <= float(6) && float(7) == float(8) || float(8) != float(9)]";
    public static final String OP_FLOAT_INVALID = "[float(1) && float(2)]";

    public static final String OP_DOUBLE_BASIC = "[(11.0 / 2.0) + (3.0 * 5.0) + (-9.0 + 1.0) + (4.0 - 14.0) + (13.0 % 5.0)]";
    public static final String OP_DOUBLE_POWER = "[2.0 ^ 5.0]";
    public static final String OP_DOUBLE_COMPARISON = "[1.0 > 2.0 && 2.0 >= 3.0 || 3.0 < 4.0 || 5.0 <= 6.0 && 7.0 == 8.0 || 8.0 != 9.0]";
    public static final String OP_DOUBLE_INVALID = "[1.0 && 2.0]";

    public static final String OP_BOOLEAN_COMPARISON = "[true > false && false >= true || true < false || false <= false && true == true || false != true]";
    public static final String OP_BOOLEAN_INVALID = "[true + false]";

    public static final String OP_DATE_BASIC = "[now() + 1 - now()]";
    public static final String OP_DATE_COMPARISON = "[now() < now() + 1 && now() <= now() - 1 || now() > now() - 1 || now() >= now() + 1 && now() == now() || now() != now()]";
    public static final String OP_DATE_INVALID = "[now() / 5]";
    public static final String OP_DATE_UNARY = "[!now()]";

    public static final String OP_STRING_BASIC = "[\"con\" + \"cat\" + \"enation\"]";
    public static final String OP_STRING_COMPARISON = "[\"ab\" > \"cd\" && \"ef\" >= \"gh\" || \"ij\" < \"kl\" || \"mn\" <= \"op\" && \"qr\" == \"st\" || \"uv\" != \"wx\"]";
    public static final String OP_STRING_INVALID = "[\"ab\" && \"cd\"]";

    public static final String OP_PROMOTE_BOOLEAN = "[(true + byte(1)) + (true + short(2)) + (false + 3) + (true + long(4)) + (false + float(5)) + (true + 6.0) + (false + \"\")]";
    public static final String OP_PROMOTE_BOOLEAN_TO_DATE = "[false + now()]";
    public static final String OP_PROMOTE_BOOLEAN_TO_ANY = "[false + typeOf(1)]";
    public static final String OP_PROMOTE_DATE = "[\"Now: \" + now()]";
    public static final String OP_PROMOTE_TO_SHORT = "[short(2) + byte(1)]";
    public static final String OP_PROMOTE_TO_INT = "[3 + short(2) + byte(1)]";
    public static final String OP_PROMOTE_TO_LONG = "[long(4) + 3 + short(2) + byte(1)]";
    public static final String OP_PROMOTE_TO_FLOAT = "[float(5) + long(4) + 3 + short(2) + byte(1)]";
    public static final String OP_PROMOTE_TO_DOUBLE = "[6.0 + float(5) + long(4) + 3 + short(2) + byte(1)]";

    public static final String OP_DIVISION_BY_ZERO = "[1 / false]";

    public static final String OP_NULL_EQUALITY = "[null == null]";
    public static final String OP_NULL_INEQUALITY = "[null != null]";
    public static final String OP_NULL_INVALID = "[null > null]";
    public static final String OP_NULL_UNARY_MINUS = "[-null]";

    public static final String CUSTOM_FUNCTION = "Factorial of [10] is [factorial(10)]";
    public static final String VARIABLES_AND_MEMBER_REFERENCE = "The distance between points [a] and [b] is [sqrt(pow(a.x - b.x) + pow(a.y - b.y))].";
    public static final String TRIPLE_MEMBER_REFERENCE = "[code.identifiers.class]";
    public static final String PRIVATE_MEMBER_REFERENCE = "[code.length]";
    public static final String INVALID_MEMBER_REFERENCE = "[some.invalid.reference]";
    public static final String INVALID_MEMBER_REFERENCE2 = "[code.x]";
    public static final String FUNCTION_MEMBER_REFERENCE = "[getPoint().x]";
    public static final String FUNCTION_INVALID_MEMBER_REFERENCE = "getPoint().z";
    public static final String TARGET_MEMBER_REFERENCE = "[point.x + this.point.y]";
    public static final String TARGET_MEMBER_INVALID_REFERENCE_1 = "[point.x + this.point.z]";
    public static final String TARGET_MEMBER_INVALID_REFERENCE_2 = "[missing.x + this.missing]";
    public static final String TARGET_MEMBER_REFERENCE2 = "[point]";
    public static final String INVALID_IDENTIFIER = "[missing]";
    public static final String IDENTIFIERS = "[simple + some.reference / simple]";
    public static final String RESOLVE_OBJECT = "[myObject.x]";

    public static final String FORMAT_DOUBLE = "[1.2345 @ \"#.00\"]";
    public static final String FORMAT_INTEGER = "[12345 @ \"#\"]";
    public static final String FORMAT_DATE = "[now() @ \"YYYY/MM/dd\"]";
    public static final String FORMAT_DATE_DEFAULT = "[now() @ \"\"]";
    public static final String FORMAT_NULL = "[null @ \"#.00\"]";
    public static final String FORMAT_OBJECT = "[getPoint() @ \"\"]";
    public static final String FORMAT_CULTURE = "[1.2345 @ \"#.00\" : \"de\"]";
    public static final String FORMAT_INVALID = "[12345 @ ]";
    public static final String FORMAT_CULTURE_INVALID = "[1.2345 @ \"#.00\" : ]";
    public static final String DISCARD = "[1.2345 @ !]";

    public static final String QUANTITY = "[24pt + 20.1px]";

    public static final String DISPATCH_FUNCTION_CALL = "[customFunction()]";
    public static final String DISPATCH_FUNCTION_CALL_MISSING_FUNCTION = "[missingFunction()]";
    public static final String PARSE_OBJECT = "[#ff0123]";
    public static final String PARSE_OBJECT2 = "[#123]";

    public static final String NORMALIZED = "[1 + 2]";
    public static final String NOT_NORMALIZED = "1 + 2";
}