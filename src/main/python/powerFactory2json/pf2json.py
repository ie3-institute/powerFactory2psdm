import powerfactory  # @UnresolvedImport @UnusedVariable
import json
import inspect
import os
import re
import pf2jsonUtils
# fixme: Delete reload after development
import importlib
importlib.reload(pf2jsonUtils)
from pf2jsonUtils import attributes4export, elements4export, nested_elements4export, reserved_keywords

#################
# Configuration #
#################

exported_grid_dir = "../pfGridExport"
exported_grid_file = os.path.join(exported_grid_dir, "pfGrid.json")

##########
# Script #
##########

def name_without_preamble(full_name):
    """
    Remove name pollution by omitting uniform file path preamble
    """
    match = re.search('(?<=Network Data\.IntPrjfolder\\\\|Type Library\.IntPrjfolder\\\\).*', full_name)
    return match.group() if match is not None else full_name

def safe_name(unsafe_str):
    if unsafe_str in reserved_keywords or unsafe_str.endswith('_'):
        return f"{unsafe_str}_safe"  # only way to avoid auto generation of scala class adding backticks or similar
    return unsafe_str

def to_camel_case(snake_str):
    components = snake_str.split('_')
    # We capitalize the first letter of each component except the first one
    # with the 'title' method and join them together.
    return components[0] + ''.join(x.title() for x in components[1:])


def get_attribute_dict(raw_element, attributes_to_include, append_type=False):
    """
    Creates a dict which includes all members/fields noted in included_fields of a given raw PowerFactory element.
    """
    element = {"id": name_without_preamble(raw_element.GetFullName())}
    for member in inspect.getmembers(raw_element):
        if not (
                member[0].startswith('_')
                and inspect.ismethod(member[1])
                and isinstance(member[1], powerfactory.Method)
                and inspect.isclass(member[1])
        ) and member[0] in attributes_to_include:
            if not isinstance(member[1], powerfactory.DataObject):
                element[to_camel_case(safe_name(member[0]))] = member[1]
            elif isinstance(member[1], powerfactory.DataObject) and member[0] in nested_elements4export:
                element[to_camel_case(safe_name(member[0]))] = get_attribute_dicts([member[1]], attributes4export[member[0]])
    if append_type:
        element["pfCls"] = raw_element.GetClassName()
    return element


def get_attribute_dicts(raw_elements, attributes_to_include):
    """
    Creates a list with an attribute dictionary for each raw PowerFactory element
    """
    elements = []
    single_node_connection = ["ElmLod", "ElmLodlv", "ElmLodmv", "ElmPvsys", "ElmSym", "ElmGenstat", "ElmXnet"]
    edges = ["ElmLne", "ElmCoup"]
    typed_models = ["ElmLne", "ElmTr2"]
    for raw_element in raw_elements:
        element_class = raw_element.GetClassName()
        element = get_attribute_dict(raw_element, attributes_to_include)
        element_id = name_without_preamble(raw_element.GetFullName())

        # export connected elements of nodes
        if element_class == "ElmTerm":
            element["conElms"] = []
            for con_elm in raw_element.GetConnectedElements():
                element["conElms"].append(get_attribute_dict(con_elm, attributes4export["conElms"], True))

        # if element is a 2 winding transformer
        if (element_class == "ElmTr2"):
            try:
                element["busHvId"] = name_without_preamble(raw_element.bushv.cterm.GetFullName())
            except Exception:
                app.PrintWarn(f"Could not determine the id of the hv-bus for transformer {element_id}")
                element["busHvId"] = None
            try:
                element["busLvId"] = name_without_preamble(raw_element.buslv.cterm.GetFullName())
            except Exception:
                app.PrintWarn(f"Could not determine the id of the lv-bus for transformer {element_id}")
                element["busLvId"] = None
            try:
                element["cPtapc"] = name_without_preamble(raw_element.c_ptapc.GetFullName())
            except Exception:
                element["cPtapc"] = None

        if element_class in edges:
            # export ids of nodes the edges are connected to
            try:
                element["bus1Id"] = name_without_preamble(raw_element.bus1.cterm.GetFullName())
            except Exception:
                app.PrintWarn(f"Could not determine the first bus id of element {element_id}")
                element["bus1Id"] = None
            try:
                element["bus2Id"] = name_without_preamble(raw_element.bus2.cterm.GetFullName())
            except Exception:
                app.PrintWarn(f"Could not determine the second bus id of element {element_id}")
                element["bus2Id"] = None

        if element_class in single_node_connection:
            try:
                element["busId"] = name_without_preamble(raw_element.bus1.cterm.GetFullName())
            except Exception:
                app.PrintWarn(f"Could not determine bus-id of element {element_id}")
                element["busId"] = None

        if element_class in typed_models:
            try:
                element["typeId"] = name_without_preamble(raw_element.typ_id.GetFullName())
            except:
                app.PrintWarn(f"Could not determine the type-id of element {element_id}")
                element["typeId"] = None

        elements.append(element)
    return elements


app = powerfactory.GetApplication()
app.EchoOff()
project = app.GetActiveProject()
pfGrid = {}  # resulting pf grid json export

# get general settings
pfGrid.update({"projectSettings":
    [{
        "unitSystem": project.ilenunit,
        "prefixPQS": project.cspqexp,
        "prefixLength": project.clenexp
    }]
})

app.PrintInfo("Starting to generate the JSON export")

# generate json strings
for element_name in elements4export:
    pfGrid.update({element_name: get_attribute_dicts(app.GetCalcRelevantObjects(elements4export[element_name]),
                                                     attributes4export[element_name])})
app.PrintInfo("Writing that stuff into a file")

# write
if not os.path.exists(exported_grid_dir):
    os.makedirs(exported_grid_dir)

with open(exported_grid_file, 'w') as f:
    json.dump(pfGrid, f, indent= 2)

app.PrintInfo("I'm done. Where is my money?")
app.EchoOn()