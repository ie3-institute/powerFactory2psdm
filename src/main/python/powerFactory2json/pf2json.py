import powerfactory  # @UnresolvedImport @UnusedVariable
import json
import inspect
import os
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
    flags = ["\\Network Data.IntPrjfolder\\", "\\Equipment Type Library.IntPrjfolder\\"]
    for flag in flags:
        if flag in full_name:
            return full_name.split(flag)[-1]
    return full_name

def safe_name(unsafe_str):
    if unsafe_str in reserved_keywords or unsafe_str.endswith('_'):
        return f"{unsafe_str}_safe"  # only way to avoid auto generation of scala class adding backticks or similar
    return unsafe_str

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
                element[safe_name(member[0])] = member[1]
            elif isinstance(member[1], powerfactory.DataObject) and member[0] in nested_elements4export:
                element[safe_name(member[0])] = get_attribute_dicts([member[1]], attributes4export[member[0]])
    if append_type:
        element["pfCls"] = raw_element.GetClassName()
    return element


def get_attribute_dicts(raw_elements, attributes_to_include):
    """
    Creates a list with an attribute dictionary for each raw PowerFactory element
    """
    elements = []
    pf_edges = ["ElmLne", "ElmCoup"]
    for raw_element in raw_elements:
        element = get_attribute_dict(raw_element, attributes_to_include)

        # export connected elements of nodes and transformers
        if (raw_element.GetClassName() in ["ElmTerm", "ElmTr2", "ElmTr3"]):
            element["conElms"] = []
            for con_elm in raw_element.GetConnectedElements():
                element["conElms"].append(get_attribute_dict(con_elm, attributes4export["conElms"], True))

        # export ids of nodes the edges are connected to
        if (raw_element.GetClassName() in pf_edges):
            try:
                element["bus1Id"] = name_without_preamble(raw_element.bus1.cterm.GetFullName())
            except Exception:
                element["bus1Id"] = None
            try:
                element["bus2Id"] = name_without_preamble(raw_element.bus2.cterm.GetFullName())
            except Exception:
                element["bus2Id"] = None

        elements.append(element)
    return elements


dpf = powerfactory.GetApplication()
dpf.EchoOff()
pfGrid = {}  # resulting pf grid json export

# generate json strings
for element_name in elements4export:
    pfGrid.update({element_name: get_attribute_dicts(dpf.GetCalcRelevantObjects(elements4export[element_name]),
                                                     attributes4export[element_name])})

# write
if not os.path.exists(exported_grid_dir):
    os.makedirs(exported_grid_dir)

with open(exported_grid_file, 'w') as f:
    json.dump(pfGrid, f, indent= 2)