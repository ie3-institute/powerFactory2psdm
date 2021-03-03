import powerfactory  # @UnresolvedImport @UnusedVariable
import json
import inspect

#################
# Configuration #
#################

exported_grid_file = 'pfGrid.json'

elements4export = {
    'nodes': '*.ElmTerm',
    'lines': '*.ElmLne',
    'lineTypes': '*.TypLne',
    'trafos2w': '*.ElmTr2',
    'trafoTypes2w': '*.TypTr2',
    'substations': '*.ElmSubstat',
    'loads': '*.ElmLod',
    'loadsLV': '*.ElmLodlv',
    'loadsMV': '*.ElmLodmv',
    'statGen': '*.ElmGenstat',  # heat pumps, storage, electric vehicles, photovoltaic units ...
    'extGrid': '*.ElmXNet',
    'fuses': '*.RelFuse',
    'powerPlants': '*.ElmSym',  # renewable power plants
    'pvs': '*.ElmPvsys'  # additional photovoltaic units
}

excluded_fields = {
    'nodes': [],
    'lines': ['tmat'],
    'lineTypes': [],
    'trafos2w': ['mTaps', 'coldloadtab2'],
    'trafoTypes2w': [],
    'substations': [],
    'loads': [],
    'loadsLV': [],
    'loadsMV': [],
    'statGen': [],
    'extGrid': [],
    'fuses': [],
    'powerPlants': [],
    'pvs': [],
}

##########
# Script #
##########

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
                     "wait", "notify", "toString", "notifyAll", "hashCode", "getClass", "finalize", "equals", "clone"]


def safe_name(unsafe_str):
    if unsafe_str in reserved_keywords or unsafe_str.endswith('_'):
        return f"{unsafe_str}_safe"  # only way to avoid auto generation of scala class adding backticks or similar
    return unsafe_str


def json_elements(raw_elements, invalid_field_names):
    elements = []
    for raw_element in raw_elements:
        element = {}
        for i in inspect.getmembers(raw_element):
            if not i[0].startswith('_'):
                if not inspect.ismethod(i[1]):
                    if not isinstance(i[1], powerfactory.Method):
                        if not isinstance(i[1], powerfactory.DataObject):
                            if not inspect.isclass(i[1]):
                                if not i[0] in invalid_field_names:
                                    element.update({safe_name(i[0]): i[1]})
        elements.append(element)
    return elements


dpf = powerfactory.GetApplication()
dpf.EchoOff()
pfGrid = {}  # resulting pf grid json export

# generate json strings
for element_name in elements4export:
    pfGrid.update({element_name: json_elements(dpf.GetCalcRelevantObjects(elements4export[element_name]),
                                               excluded_fields[element_name])})
# write
with open(exported_grid_file, 'w') as f:
    json.dump(pfGrid, f)
