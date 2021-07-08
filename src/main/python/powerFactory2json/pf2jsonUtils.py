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
    'extGrid': '*.ElmXnet',
    'powerPlants': '*.ElmSym',  # renewable power plants
    'pvs': '*.ElmPvsys',  # additional photovoltaic units
    'switches': '*.ElmCoup'
}

nested_elements4export = {'cpZone', 'cpArea'}


attributes4export = {
    'conElms': [],
    'cpZone': [],
    'cpArea': [],
    'nodes': [
        "vtarget",
        "uknom",
        "GPSlat",
        "GPSlon"
    ],
    'lines': [],
    'lineTypes': [
        "rline",
        "xline",
        "gline",
        "bline",
        "sline",
        "uline"
    ],
    'trafos2w': [],
    'switches':[],
    'trafoTypes2w': [],
    'trafos3w': [],
    'trafoTypes3w': [],
    'loads': [],
    'loadsLV': [],
    'loadsMV': [],
    'statGen': [
        'cCategory',
        'orient',
        'tilt',
        'inveff'
    ],
    'circuitBreaker' : [],
    'extGrid': [],
    'powerPlants': [],
    'pvs': [
        'orient',
        'tilt',
        'inveff'
    ]
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
