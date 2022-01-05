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
    'switches': '*.ElmCoup',
    'lineSections': '*.ElmLnesec'
}


attributes4export = {
    'lineSections': [
        "dline"
    ],
    'conElms': [],
    'nodes': [
        "vtarget",
        "uknom",
        "GPSlat",
        "GPSlon"
    ],
    'lines': [
        "dline",
        "GPScoords",
        "cubsecs"
    ],
    'lineTypes': [
        "rline",
        "xline",
        "gline",
        "bline",
        "sline",
        "uline",
    ],
    'trafos2w': [
        "nntap",
        "ntrcn",
    ],
    'trafoTypes2w': [
        "strn",
        "utrn_h",
        "utrn_l",
        "dutap",
        "phitr",
        "tap_side",
        "nntap0",
        "ntpmn",
        "ntpmx",
        "uktr",
        "pcutr",
        "pfe",
        "curmg"
    ],
    'switches':[
        "on_off"
    ],
    'trafos3w': [],
    'trafoTypes3w': [],
    'loads': [
        'slini',
        'coslini',
        'pf_recap',
        'i_scale',
        'scale0'
    ],
    'loadsLV': [
        'slini',
        'coslini',
        'pf_recap',
        'i_scale',
        'scale0'

    ],
    'loadsMV': [
        'slini',
        'coslini',
        'pf_recap',
        'i_scale',
        'scale0'
    ],
    'statGen': [
        'sgn',
        'sgini',
        'cosgini',
        'cosn',
        'pf_recap',
        'cCategory'
    ],
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
