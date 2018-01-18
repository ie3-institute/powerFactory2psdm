'''
@author: Kittl
'''
# ====== Prepare code debugging ======
# import sys
# sys.path.append \
# ("C:\\Users\\Kittl\\.p2\\pool\\plugins\\org.python.pydev_4.5.4.201601292234\\pysrc")
# # ("C:\\Program Files\\eclipse\\plugins\\org.python.pydev_2.8.2.2013090511\\pysrc")
# import pydevd #@UnresolvedImport
# #start debug
# pydevd.settrace()

# ====== Import packages ======
import powerfactory #@UnresolvedImport @UnusedVariable
from re import subn, search, finditer
import os
from os import makedirs
from os.path import exists, isdir
import sqlalchemy as sa
import sys
from imp import reload  # for reloading modules to compile correctly without restarting PowerFactory

# ====== Import self written code ======
from exportHelpers import tables, colHeads, convertExportDestinations, convertExportProfile, getDpfPrefixes
from tableHandling import writeToXlsx, writeToCsv#, writeToDb
import exportExternalNets
import exportLines
import exportLineTypes
import exportLoads
import exportNodes
import exportPowerPlants
import exportRES
import exportStorages
import exportStorageTypes
import exportSubstations
import exportSwitches
import exportTansformerTypes
import exportTransformers
import exportEV
import exportHP

# ====== "Global" variables ======
app = powerfactory.GetApplication()
app.EchoOff()
project = app.GetActiveProject()
getDpfPrefixes(project)
ldfObj = app.GetFromStudyCase('ComLdf')
scriptPath = os.path.dirname(os.path.abspath(__file__)) # get path of the running script
sys.path.append(scriptPath)

# --- reload altered submodules for correct compilation without closing PowerFactory
reload(exportLoads)

# --- Check export destination
thisScript = app.GetCurrentScript()     # Get this script
exportDestinationRaw = thisScript.exportDestination
exportDestination = convertExportDestinations(exportDestinationRaw)
if exportDestination is False:
    app.PrintError('The chosen exportProfile "%s" does not exist!' % exportDestinationRaw)

# --- Check export profile
exportProfileRaw = thisScript.exportProfile
exportProfile = convertExportProfile(exportProfileRaw)
if exportProfile is False:
    app.PrintError('The chosen exportProfile "%s" does not exist!' % exportProfileRaw)

# ====== Process the internal parameters ======
if exportDestination is 1 or exportDestination is 2:
    filenameRaw = project.loc_name          # Filename is supposed to be the project name
    folderRaw = thisScript.folder
    
    # --- filename
    posDot = [m.start() for m in finditer("\.", filenameRaw)]
    cntDot = len(posDot)
    
    if cntDot is not 0:
        # Cut the substring after with last dot, this is most possibly a file extension
        filename = filenameRaw[0:max(posDot)]
    else:
        filename = filenameRaw
    
    # Remove german umlauts
    filename = filename.replace('ä',u'ae')
    filename = filename.replace('Ä',u'Ae')
    filename = filename.replace('ö',u'oe')
    filename = filename.replace('Ö',u'Oe')
    filename = filename.replace('ü',u'ue')
    filename = filename.replace('Ü',u'Ue')
    filename = filename.replace('ß',u'ss')
    filename = subn('[\.:!"Â§$%&/()=?|\\\\\+\-\*/\s]', '_', filename)
    # The backslash has to be escaped and both backslashes have to be escaped once again to achieve a string literal...
    filename = filename[0]
    
    # --- Add the active network variation as suffix to the filename
    networkVar = app.GetActiveNetworkVariations()
    if len(networkVar) > 1:
        app.PrintWarn('More than one network variations are activated! Take the first one.')
    if len(networkVar) == 0:
        varSuffix = ""
    else:
        varSuffix = '_' + networkVar[0].loc_name
    # Remove german umlauts
    varSuffix = varSuffix.replace('ä',u'ae')
    varSuffix = varSuffix.replace('Ä',u'Ae')
    varSuffix = varSuffix.replace('ö',u'oe')
    varSuffix = varSuffix.replace('Ö',u'Oe')
    varSuffix = varSuffix.replace('ü',u'ue')
    varSuffix = varSuffix.replace('Ü',u'Ue')
    varSuffix = varSuffix.replace('ß',u'ss')
    varSuffix = subn('[\.:!"Â§$%&/()=?|\\\\\+\-\*/\s]', '_', varSuffix)
    # The backslash has to be escaped and both backslashes have to be escaped once again to achieve a string literal...
    varSuffix = varSuffix[0]
    
    filename = filename + varSuffix
        
    
    # --- folder name
    folder = subn('[\.!"Â§$%&/()=?|]', '_', folderRaw)
    folder = folder[0]
    # Remove german umlauts
    folder = folder.replace('Ã¤',u'ae')
    folder = folder.replace('Ã¶',u'oe')
    folder = folder.replace('Ã¼',u'ue')
    folder = folder.replace('ÃŸ',u'ss')
    # Add a backslash at the end of the folder-string
    if folder[len(folder)-1] is not "\\":
        folder += "\\"
    folderStart = search("[A-za-z]:\\\\", folder)
    if folderStart is None or folderStart.start() is not 0:
        app.PrintError('The folder path is not a valid path!')
    
    # --- Check if folder and file already exist
    if not exists(folder):
        app.PrintInfo('The folder "%s" does not exist. I create it!' % folder)
        makedirs(folder)
    elif not isdir(folder):
        app.PrintError('The folder "%s" does exist, but is not a folder! Wuuuaahhh!')
    
    fullpath = folder + filename
elif exportDestination is 3:  # 3 means MSSQL-Export
    # ====== Establish database connection ======
    dbUser = thisScript.username
    dbPass = thisScript.password
    dbOdbc = thisScript.odbcConnection
    dbEngine = sa.create_engine('mssql+pyodbc://' + dbUser + ':' + dbPass + '@' + dbOdbc, echo=True)
    # There is no database connection right now. It is only established with the first operation at the database
    try:
        dbConnection = dbEngine.connect()
    except sa.exc.SQLAlchemyError:
        app.PrintError('Database Connection can not be established!')
        # raise Exception('Database Connection can not be established!')
        exit()

    # http://www.paulsprogrammingnotes.com/2014/01/clonecopy-table-schema-from-one.html
    dbEngine._metadata = sa.MetaData(bind=dbEngine)
    dbEngine._metadata.reflect(dbEngine)

# ====== Export the grid topology ======
app.PrintPlain('Starting to collect the topological data.')
#app.PrintPlain('Export profile: '+convertExportProfile(exportProfile))
# Create a list of empty lists. The number of empty lists is equal to the number of tables.
noOfTables = len(tables[exportProfile-1])
topology = [None] * noOfTables
(idx, topMember) = exportSubstations.exportSubstations(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Substations successfully exported.")

(idx, topMember) = exportNodes.exportNodes(app, ldfObj, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Nodes successfully exported.")

(idx, topMember) = exportLines.exportLines(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Lines successfully exported.")

(idx, topMember) = exportTransformers.exportTransformers(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Transformers successfully exported.")

(idx, topMember) = exportSwitches.exportSwitches(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Switches successfully exported.")

(idx, topMember) = exportLoads.exportLoads(app, ldfObj, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Loads successfully exported.")

(idx, topMember) = exportStorages.exportStorages(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Storages successfully exported.")

(idx, topMember) = exportRES.exportRES(app, ldfObj, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("RES successfully exported.")

(idx, topMember) = exportPowerPlants.exportPowerPlants(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Power plants successfully exported.")

(idx, topMember) = exportExternalNets.exportExternalNets(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("External nets successfully exported.")

(idx, topMember) = exportEV.exportEV(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Electric vehicles successfully exported.")

(idx, topMember) = exportHP.exportHP(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Heat pumps successfully exported.")

(idx, topMember) = exportLineTypes.exportLineTypes(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Line types successfully exported.")

(idx, topMember) = exportTansformerTypes.exportTransformerTypes(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Transformer types successfully exported.")

(idx, topMember) = exportStorageTypes.exportStorageTypes(app, exportProfile, tables, colHeads)
if not topMember is None:
    topology[idx] = topMember
    app.PrintPlain("Storage types uccessfully exported.")

# ====== Save the topology =====
app.PrintInfo("Write results to the defined destination.")
if exportDestination is 1:
    # ------ Save to Excel workspace ------
    writeRes = writeToXlsx(app, topology, fullpath, tables, exportProfile)
    if writeRes is 0:
        app.PrintInfo('Successfully written topology to "%s"!' % filename)
    else:
        app.PrintError("I wasn't able to write to the xlsx-File!")
elif exportDestination is 2:
    writeToCsv(app, topology, fullpath, tables, exportProfile)

app.EchoOn()