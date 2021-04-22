elements4export = {
    'nodes': '*.ElmTerm',
    'lines': '*.ElmLne',
    'lineTypes': '*.TypLne',
    'trafos2w': '*.ElmTr2',
    'trafoTypes2w': '*.TypTr2',
    'trafos3w': '*.ElmTr3',
    'trafoTypes3w': '*.TypTr3',
    'loads': '*.ElmLod',
    'loadsLV': '*.ElmLodlv',
    'loadsMV': '*.ElmLodmv',
    'statGen': '*.ElmGenstat',  # heat pumps, storage, electric vehicles, photovoltaic units ...
    'extGrid': '*.ElmXNet',
    'powerPlants': '*.ElmSym',  # renewable power plants
    'pvs': '*.ElmPvsys',  # additional photovoltaic units
}

nested_elements4export = {'cpZone', 'cpArea'}

fields4export = {
    'conElms': [
        "loc_name"
    ],
    'cpZone': [
        "loc_name"
    ],
    'cpArea': [
        "loc_name"
    ],
    'nodes': [
        "loc_name",
        "vtarget",
        "cpZone",
        "cpArea",
        "GPSlat",
        "GPSlon"
    ],
    'lines': [],
    'lineTypes': [],
    'trafos2w': [
        "loc_name"
    ],
    'trafoTypes2w': [],
    'trafos3w': [],
    'trafoTypes3w': [],
    'loads': [],
    'loadsLV': [],
    'loadsMV': [],
    'statGen': [],
    'circuitBreaker' : [],
    'extGrid': [],
    'powerPlants': [],
    'pvs': []
}

reserved_keywords = ["abstract",
                     "case",
                     "catch",
                     "class",
                     "def",
                     "do",
                     "else",
                     "extends",
                     "false",
                     "final",
                     "finally",
                     "for",
                     "forSome",
                     "if",
                     "implicit",
                     "import",
                     "lazy",
                     "match",
                     "new",
                     "null",
                     "object",
                     "override",
                     "package",
                     "private",
                     "protected",
                     "return",
                     "sealed",
                     "super",
                     "this",
                     "throw",
                     "trait",
                     "try",
                     "true",
                     "type",
                     "val",
                     "var",
                     "while",
                     "with",
                     "yield",
                     "wait",
                     "notify",
                     "toString",
                     "notifyAll",
                     "hashCode",
                     "getClass",
                     "finalize",
                     "equals",
                     "clone"]
