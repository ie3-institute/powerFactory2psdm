'''
@author: Kittl
'''
from enum import Enum
import sqlalchemy as sa
from builtins import int

class ExportProfiles(Enum):
    # Convention: All export profiles in lower case
    simona = 1
    simbench = 2
    matlablfr = 3

class ExportDestinations(Enum):
    # Convention: All export destinations in lower case
    excel = 1
    csv = 2
    mssql = 3
    
global ValuePrefix 
ValuePrefix = {
    # Prefixes used by PowerFactory
    "a":    1e-18,
    "f":    1e-15,
    "p":    1e-12,
    "n":    1e-9,
    "u":    1e-6,
    "m":    1e-3,
    "":     1e0,
    "k":    1e3,
    "M":    1e6,
    "G":    1e9,
    "T":    1e12,
    "P":    1e15,
    "E":    1e18
}
global prefixLength
prefixLength = 1
global prefixPQS
prefixPQS = 1


def convertExportProfile(name):
    if isinstance(name, str):
        if name.lower() in ExportProfiles.__members__:
            return ExportProfiles[name.lower()].value
        else:
            return False
    elif isinstance(name, int):
        if name in ExportProfiles.__members__:
            return ExportProfiles(name)
        else:
            return False
    else:
        return False

def convertExportDestinations(name):
    if name.lower() in ExportDestinations.__members__:
        return ExportDestinations[name.lower()].value
    else:
        return False

def correctPrefix(dpf, strPrefixDest="", strPrefixType="PQS"):
    if strPrefixType is "l":
        prefixAct = prefixLength
    elif strPrefixType is "PQS":
        prefixAct = prefixPQS
    elif strPrefixType in ValuePrefix:
        prefixAct = int( ValuePrefix[strPrefixType] )
    else:
        dpf.PrintError('Cannot handle the prefix type or the current prefix "'+strPrefixType+'". Check the arguments for the call of "correctPrefix"!')
        exit(1)

    if strPrefixDest in ValuePrefix:
        return float(prefixAct/ValuePrefix[strPrefixDest])
    else:
        dpf.PrintError('Cannot determine the prefix correction. Check the arguments for the call of "correctPrefix"!')
        exit(1)
        
def getDpfPrefixes(dpfProject):
    # Variable assignments in function are restricted to the functions sope.
    # To assign a global variable in a function you have to explicitly mark them as
    # global in the function body.
    # http://effbot.org/pyfaq/how-do-you-set-a-global-variable-in-a-function.htm
    global prefixLength, prefixPQS
    prefixLength = ValuePrefix[dpfProject.clenexp] # Prefix for all lengthes in the PowerFactory-Project
    prefixPQS = ValuePrefix[dpfProject.cspqexp] # Prefix for P, Q, S

#  ===== Define names for worksheets ======
tables = list()
# Worksheets for exportProfile 1 (Simona)
tables.append([""])
# Worksheets for exportProfile 2 (SimBench)
tables.append(
    [
        "Substation",
        "Node",
        "Line",
        "Transformer",
        "Switch",
        "Load",
        "Storage",
        "RES",
        "PowerPlant",
        "ExternalNet",
        "LineType",
        "TransformerType",
        "StorageType"
    ]
)
# Worksheets for exportProfile 3 (MatlabLFR)
tables.append(
    [
        "Knoten",
        "Leitungen",
        "Trafos",
        "Lasten",
        "StatGen",
        "EV",
        "Speicher",
        "WP",
        "Schalter"
    ]
)

# Access of colHeaders:
# 1st index:    exportProfile
# 2nd index:    index of Worksheet
# 3rd index:    index of column
# All information are stored in an sqlalchemy.Column-object regardless of the actual export destination.
# One can for example access the column header name with colHeads[x][y][z].name
colHeads = list()
# Worksheets for exportProfile 1 (Simona)
colHeads.append([""])
# Worksheets for exportProfile 2 (SimBench)
colHeads.append(
    [
        [  # Substation
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Node
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("type", sa.String(50)),
            sa.Column("vmSetp", sa.Float()),
            sa.Column("vmR", sa.Float(2)),
            sa.Column("substation", sa.String(50)),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Line
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("nodeA", sa.String(50)),
            sa.Column("nodeB", sa.String(50)),
            sa.Column("type", sa.String(50)),
            sa.Column("length", sa.Float()),
            sa.Column("nrParallel", sa.Integer()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Transformer
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("nodeA", sa.String(50)),
            sa.Column("nodeB", sa.String(50)),
            sa.Column("type", sa.String(50)),
            sa.Column("nrParallel", sa.String(50)),
            sa.Column("tappos", sa.String(50)),
            sa.Column("tapposDef", sa.String(50)),
            sa.Column("autoTap", sa.String(50)),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Switch
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("nodeA", sa.String(50)),
            sa.Column("nodeB", sa.String(50)),
            sa.Column("cond", sa.Boolean()),
            sa.Column("condDef", sa.Boolean()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Load
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("node", sa.String(50)),
            sa.Column("type", sa.String(50)),
            sa.Column("pLoad", sa.Float()),
            sa.Column("qLoad", sa.Float()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Storage
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("type",sa.String(50)),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # RES
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("node", sa.String(50)),
            sa.Column("type", sa.String(50)),
            sa.Column("pvType", sa.String(50)),
            sa.Column("wecType", sa.String(50)),
            sa.Column("bmType", sa.String(50)),
            sa.Column("pRES", sa.Float()),
            sa.Column("qRES", sa.Float()),
            sa.Column("sR", sa.Float()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Power Plant
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("node", sa.String(50)),
            sa.Column("type", sa.String(50)),
            sa.Column("pPP", sa.Float()),
            sa.Column("qPP", sa.Float()),
            sa.Column("vmControl",sa.Boolean()),
            sa.Column("sR", sa.Float()),
            sa.Column("pMin", sa.Float()),
            sa.Column("pMax", sa.Float()),
            sa.Column("qMin", sa.Float()),
            sa.Column("qMax", sa.Float()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # External Net
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("node", sa.String(50)),
            sa.Column("vmSetp", sa.Float()),
            sa.Column("vaSetp", sa.Float()),
            sa.Column("subnet", sa.String(50)),
            sa.Column("voltLvl", sa.String(50))
        ],
        [  # Line Type
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("r", sa.Float()),
            sa.Column("x", sa.Float()),
            sa.Column("b", sa.Float()),
            sa.Column("iMax", sa.Float())
        ],
        [  # Transformer Type
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("sR", sa.Float()),
            sa.Column("vmHV", sa.Float()),
            sa.Column("vmLV", sa.Float()),
            sa.Column("va0", sa.Float()),
            sa.Column("vmImp", sa.Float()),
            sa.Column("pCu", sa.Float()),
            sa.Column("pFe", sa.Float()),
            sa.Column("iNoLoad", sa.Float()),
            sa.Column("tappable", sa.Boolean()),
            sa.Column("tapside", sa.Integer()),
            sa.Column("dVm", sa.Float()),
            sa.Column("dVa", sa.Float()),
            sa.Column("tapNeutr", sa.Integer()),
            sa.Column("tapMin", sa.Integer()),
            sa.Column("tapMax", sa.Integer())
        ],
        [  # Storage Type
            sa.Column("id", sa.String(50), primary_key = True),
            sa.Column("pMax",sa.Float()),
            sa.Column("eBatt",sa.Float())
        ]
    ]
)
# Worksheets for exportProfile 1 (Simona)
colHeads.append(
    [
        [   # Knoten
            sa.Column("Knotenname", sa.String(50), primary_key = True),
            sa.Column("Spg-Regelung", sa.Integer()),
            sa.Column("Spg_SollWert[p.u.]", sa.Float()),
            sa.Column("P_Gen[MW]", sa.Float()),
            sa.Column("Q_Gen[MVA]", sa.Float()),
            sa.Column("P_Load[MW]", sa.Float()),
            sa.Column("Q_Load[MVA]", sa.Float()),
            sa.Column("U_nenn[kV]", sa.Float()),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # Leitungen
            sa.Column("Leitungsname", sa.String(50), primary_key = True),
            sa.Column("KnotenA", sa.String(50)),
            sa.Column("KnotenB", sa.String(50)),
            sa.Column("R[Ohm/km]", sa.Float()),
            sa.Column("X[Ohm/km]", sa.Float()),
            sa.Column("B[uS/km]", sa.Float()),
            sa.Column("Laenge[km]", sa.Float()),
            sa.Column("Anzahl", sa.Integer()),
            sa.Column("Inenn[A]", sa.Float()),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # Trafos
            sa.Column("Trafoname", sa.String(50), primary_key = True),
            sa.Column("KnotenA", sa.String(50)),
            sa.Column("KnotenB", sa.String(50)),
            sa.Column("Smax[MVA]", sa.Float()),
            sa.Column("U_OS[kV]", sa.Float()),
            sa.Column("U_US[kV]", sa.Float()),
            sa.Column("U_k[prozent]", sa.Float()),
            sa.Column("P_Kupferverluste[kW]", sa.Float()),
            sa.Column("P_Eisenverluste[kW]", sa.Float()),
            sa.Column("Leerlaufstrom[prozent]", sa.Float()),
            sa.Column("Tapposition", sa.Integer()),
            sa.Column("Tapseite", sa.Integer()),
            sa.Column("du[prozent]", sa.Float()),
            sa.Column("dphi[Grad]", sa.Float()),
            sa.Column("tap neutrale Position", sa.Integer()),
            sa.Column("Tap_mx", sa.Integer()),
            sa.Column("Tap_min", sa.Integer()),
            sa.Column("parallele Anzahl", sa.Integer()),
            sa.Column("Auto_Stufung", sa.Boolean()),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # Lasten
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("Knoten", sa.String(50)),
            sa.Column("Scheinleistung[kVA]", sa.Float()),
            sa.Column("cosphi", sa.Float()),
            sa.Column("Jahreswert[kWh]", sa.Float()),
            sa.Column("DSM", sa.Boolean()),
            sa.Column("Szenario", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # StatGen
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("Knoten", sa.String(50)),
            sa.Column("Scheinleistung[MVA]", sa.Float()),
            sa.Column("cosphi", sa.Float()),
            sa.Column("Typ", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # EV
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("Knoten", sa.String(50)),
            sa.Column("Typ", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spannungsebene", sa.String(50))
        ],
        [   # Speicher
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("Knoten", sa.String(50)),
            sa.Column("Typ", sa.String(50)),
            sa.Column("Verhalten", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # WP
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("Knoten", sa.String(50)),
            sa.Column("Typ", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ],
        [   # Schalter
            sa.Column("ID", sa.String(50), primary_key = True),
            sa.Column("KnotenA", sa.String(50)),
            sa.Column("KnotenB", sa.String(50)),
            sa.Column("Zustand", sa.String(50)),
            sa.Column("Teilnetz", sa.String(50)),
            sa.Column("Spgsebene", sa.String(50))
        ]
    ]
)