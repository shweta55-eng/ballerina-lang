import testorg/foo version v1;

type Ballerina "Ballerina";

function testAccessConstantWithoutType() returns Ballerina {
    return foo:constName;
}

function testAccessConstantWithoutTypeAsString() returns string {
    return foo:constName;
}

type Colombo "Colombo";

function testAccessConstantWithType() returns Colombo {
    return foo:constAddress;
}

type AB "A"|"B";

function testAccessFiniteType() returns foo:AB {
    return foo:A;
}

// Todo - Enable after fixing https://github.com/ballerina-platform/ballerina-lang/issues/11148
// function testReturnFiniteType() returns AB {
//     return foo:A; // Valid because this is same as `return "A";`
// }

function testAccessTypeWithContInDef() returns foo:CD {
    foo:CD c = "C";
    return c;
}

// -----------------------------------------------------------

function testConstInMapKey() returns string {
    string key = foo:KEY;
    map<string> m = { key: "value" };
    return m.key;
}

function testConstInMapValue() returns string {
    string value = foo:VALUE;
    map<string> m = { "key": value };
    return m.key;
}

function testConstInJsonKey() returns json {
    string key = foo:KEY;
    json j = { key: "value" };
    return j.key;
}

function testConstInJsonValue() returns json {
    string value = foo:VALUE;
    json j = { "key": value };
    return j.key;
}

// -----------------------------------------------------------

function testBooleanConstInUnion() returns any {
    boolean|int v = foo:booleanWithType;
    return v;
}

function testIntConstInUnion() returns any {
    int|boolean v = foo:intWithType;
    return v;
}

function testByteConstInUnion() returns any {
    byte|boolean v = foo:byteWithType;
    return v;
}

function testFloatConstInUnion() returns any {
    float|boolean v = foo:floatWithType;
    return v;
}

function testStringConstInUnion() returns any {
    string|boolean v = foo:stringWithType;
    return v;
}

// -----------------------------------------------------------

function testBooleanConstInTuple() returns boolean {
    (boolean, int) v = (foo:booleanWithType, 1);
    return v[0];
}

function testIntConstInTuple() returns int {
    (int, boolean) v = (foo:intWithType, true);
    return v[0];
}

function testByteConstInTuple() returns byte {
    (byte, boolean) v = (foo:byteWithType, true);
    return v[0];
}

function testFloatConstInTuple() returns float {
    (float, boolean) v = (foo:floatWithType, true);
    return v[0];
}

function testStringConstInTuple() returns string {
    (string, boolean) v = (foo:stringWithType, true);
    return v[0];
}

// -----------------------------------------------------------

// Todo - Enable after fixing https://github.com/ballerina-platform/ballerina-lang/issues/11183.
//type M record { string f; }|Z;
//
//M m1 = { f: "foo" };
//
//M m2 = "V";
//
//M m3 = "W";
//
//M m4 = "X";

type Y X;

type Z "V"|W|X;

Y y = "X";

Z z1 = "V";

Z z2 = "W";

Z z3 = "X";


const string W = "W";

const string X = "X";