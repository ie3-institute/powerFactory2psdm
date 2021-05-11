import powerfactory  # @UnresolvedImport @UnusedVariable
import json
import inspect
import os
import pf2jsonUtils
import imp
imp.reload(pf2jsonUtils)
from pf2jsonUtils import fields4export, elements4export, nested_elements4export, reserved_keywords



#################
# Configuration #
#################

exported_grid_dir = "../pfGridExport"
exported_grid_file = os.path.join(exported_grid_dir, "pfGrid.json")

##########
# Script #
##########

def safe_name(unsafe_str):
    if unsafe_str in reserved_keywords or unsafe_str.endswith('_'):
        return f"{unsafe_str}_safe"  # only way to avoid auto generation of scala class adding backticks or similar
    return unsafe_str

def get_members(raw_element, included_fields, append_type=False):
    element = {"uid": raw_element.GetFullName()}
    for i in inspect.getmembers(raw_element):
        if not i[0].startswith('_'):
            if not inspect.ismethod(i[1]):
                if not isinstance(i[1], powerfactory.Method):
                    if not inspect.isclass(i[1]):
                        if i[0] in included_fields:
                            if not isinstance(i[1], powerfactory.DataObject):
                                element[safe_name(i[0])] = i[1]
                            elif isinstance(i[1], powerfactory.DataObject) and i[0] in nested_elements4export:
                                element[safe_name(i[0])] = json_elements([i[1]], fields4export[i[0]])
    if append_type:
        element["pfCls"] = raw_element.GetClassName()
    return element

def json_elements(raw_elements, included_fields):
    elements = []
    for raw_element in raw_elements:
        element = get_members(raw_element, included_fields)

        # export connected elements of nodes, transformers and lines to get grid topology
        if (raw_element.GetClassName() in ["ElmTerm", "ElmTr2", "ElmTr3", "ElmLne"]):
            element["conElms"] = []
            for con_elm in raw_element.GetConnectedElements():
                element["conElms"].append(get_members(con_elm, fields4export["conElms"], True))

        elements.append(element)
    return elements


dpf = powerfactory.GetApplication()
dpf.EchoOff()
pfGrid = {}  # resulting pf grid json export

# generate json strings
for element_name in elements4export:
    pfGrid.update({element_name: json_elements(dpf.GetCalcRelevantObjects(elements4export[element_name]),
                                               fields4export[element_name])})

# write
if not os.path.exists(exported_grid_dir):
    os.makedirs(exported_grid_dir)

with open(exported_grid_file, 'w') as f:
    json.dump(pfGrid, f, indent= 2)