import powerfactory  # @UnresolvedImport @UnusedVariable
import json
import inspect
import os
from pf2jsonUtils import excluded_fields, elements4export, nested_elements4export, reserved_keywords

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


def json_elements(raw_elements, invalid_field_names):
    elements = []
    for raw_element in raw_elements:
        element = {}
        for i in inspect.getmembers(raw_element):
            if not i[0].startswith('_'):
                if not inspect.ismethod(i[1]):
                    if not isinstance(i[1], powerfactory.Method):
                        if not inspect.isclass(i[1]):
                            if not i[0] in invalid_field_names:
                                if not isinstance(i[1], powerfactory.DataObject):
                                    element.update({safe_name(i[0]): i[1]})
                                elif isinstance(i[1], powerfactory.DataObject) and i[0] in nested_elements4export:
                                    element.update({safe_name(i[0]): json_elements([i[1]], excluded_fields[i[0]])})
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
if not os.path.exists(exported_grid_dir):
    os.makedirs(exported_grid_dir)

with open(exported_grid_file, 'w') as f:
    json.dump(pfGrid, f, indent= 2)