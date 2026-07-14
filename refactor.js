const fs = require('fs');
const path = require('path');

const BASE_DIR = path.resolve('src/main/java/me/robomonkey/versus');
const BASE_PKG = "me.robomonkey.versus";

const MAPPING = {
    "RootVersusCommand.java": "command/RootVersusCommand.java",
    "Versus.java": "Versus.java",
    "arena/Arena.java": "arena/model/Arena.java",
    "arena/ArenaBuilder.java": "arena/editor/ArenaBuilder.java",
    "arena/ArenaBuilderCoordinator.java": "arena/editor/ArenaBuilderCoordinator.java",
    "arena/ArenaEditor.java": "arena/editor/ArenaEditor.java",
    "arena/ArenaKitsGUI.java": "arena/gui/ArenaKitsGUI.java",
    "arena/ArenaManager.java": "arena/manager/ArenaManager.java",
    "arena/ArenaProperty.java": "arena/model/ArenaProperty.java",
    "arena/ArenaRollbackManager.java": "arena/manager/ArenaRollbackManager.java",
    "arena/ArenaSelectionGUI.java": "arena/gui/ArenaSelectionGUI.java",
    "arena/ArenaVisibilityManager.java": "arena/manager/ArenaVisibilityManager.java",
    "arena/command/CreateCommand.java": "arena/command/CreateCommand.java",
    "arena/command/DeleteCommand.java": "arena/command/DeleteCommand.java",
    "arena/command/EditCommand.java": "arena/command/EditCommand.java",
    "arena/command/ListCommand.java": "arena/command/ListCommand.java",
    "arena/command/RootArenaCommand.java": "arena/command/RootArenaCommand.java",
    "arena/command/SetCommand.java": "arena/command/SetCommand.java",
    "arena/command/SetSpawnCommand.java": "arena/command/SetSpawnCommand.java",
    "arena/command/VisitCommand.java": "arena/command/VisitCommand.java",
    "command/AbstractCommand.java": "command/AbstractCommand.java",
    "command/DuelGroupAccept.java": "duel/command/DuelGroupAccept.java",
    "command/DuelGroupAcceptDuel.java": "duel/command/DuelGroupAcceptDuel.java",
    "command/DuelGroupCommand.java": "duel/command/DuelGroupCommand.java",
    "command/DuelGroupCreate.java": "duel/command/DuelGroupCreate.java",
    "command/DuelGroupDecline.java": "duel/command/DuelGroupDecline.java",
    "command/DuelGroupDeclineDuel.java": "duel/command/DuelGroupDeclineDuel.java",
    "command/DuelGroupDisband.java": "duel/command/DuelGroupDisband.java",
    "command/DuelGroupDuel.java": "duel/command/DuelGroupDuel.java",
    "command/DuelGroupInvite.java": "duel/command/DuelGroupInvite.java",
    "command/DuelGroupLeave.java": "duel/command/DuelGroupLeave.java",
    "command/RootCommand.java": "command/RootCommand.java",
    "cosmetics/CosmeticEffect.java": "cosmetic/model/CosmeticEffect.java",
    "cosmetics/CosmeticsManager.java": "cosmetic/manager/CosmeticsManager.java",
    "cosmetics/KillEffect.java": "cosmetic/model/KillEffect.java",
    "cosmetics/VictoryEffect.java": "cosmetic/model/VictoryEffect.java",
    "cosmetics/gui/CosmeticsMenu.java": "cosmetic/gui/CosmeticsMenu.java",
    "cosmetics/gui/KillEffectsMenu.java": "cosmetic/gui/KillEffectsMenu.java",
    "cosmetics/gui/VictoryEffectsMenu.java": "cosmetic/gui/VictoryEffectsMenu.java",
    "cosmetics/killeffects/BloodKillEffect.java": "cosmetic/model/killeffects/BloodKillEffect.java",
    "cosmetics/killeffects/ConfettiKillEffect.java": "cosmetic/model/killeffects/ConfettiKillEffect.java",
    "cosmetics/killeffects/ExplosionKillEffect.java": "cosmetic/model/killeffects/ExplosionKillEffect.java",
    "cosmetics/killeffects/LightningKillEffect.java": "cosmetic/model/killeffects/LightningKillEffect.java",
    "cosmetics/killeffects/MagicKillEffect.java": "cosmetic/model/killeffects/MagicKillEffect.java",
    "cosmetics/killeffects/NoneKillEffect.java": "cosmetic/model/killeffects/NoneKillEffect.java",
    "cosmetics/killeffects/SmokeKillEffect.java": "cosmetic/model/killeffects/SmokeKillEffect.java",
    "cosmetics/victoryeffects/DragonVictoryEffect.java": "cosmetic/model/victoryeffects/DragonVictoryEffect.java",
    "cosmetics/victoryeffects/FireworksVictoryEffect.java": "cosmetic/model/victoryeffects/FireworksVictoryEffect.java",
    "cosmetics/victoryeffects/FlamesVictoryEffect.java": "cosmetic/model/victoryeffects/FlamesVictoryEffect.java",
    "cosmetics/victoryeffects/HeartsVictoryEffect.java": "cosmetic/model/victoryeffects/HeartsVictoryEffect.java",
    "cosmetics/victoryeffects/MusicVictoryEffect.java": "cosmetic/model/victoryeffects/MusicVictoryEffect.java",
    "cosmetics/victoryeffects/NoneVictoryEffect.java": "cosmetic/model/victoryeffects/NoneVictoryEffect.java",
    "cosmetics/victoryeffects/SoulsVictoryEffect.java": "cosmetic/model/victoryeffects/SoulsVictoryEffect.java",
    "dependency/Dependencies.java": "dependency/Dependencies.java",
    "dependency/EconomyManager.java": "dependency/EconomyManager.java",
    "dependency/ItemsAdderUtil.java": "dependency/ItemsAdderUtil.java",
    "dependency/PAPIUtil.java": "dependency/PAPIUtil.java",
    "dependency/VersusPlaceholderExpansion.java": "dependency/VersusPlaceholderExpansion.java",
    "duel/BoundaryVisualizerTask.java": "duel/task/BoundaryVisualizerTask.java",
    "duel/Countdown.java": "duel/task/Countdown.java",
    "duel/Duel.java": "duel/model/Duel.java",
    "duel/DuelManager.java": "duel/manager/DuelManager.java",
    "duel/DuelState.java": "duel/model/DuelState.java",
    "duel/ReturnOption.java": "duel/model/ReturnOption.java",
    "duel/betting/BettingGUI.java": "betting/gui/BettingGUI.java",
    "duel/betting/BettingManager.java": "betting/manager/BettingManager.java",
    "duel/betting/BettingSession.java": "betting/model/BettingSession.java",
    "duel/betting/RefundManager.java": "betting/manager/RefundManager.java",
    "duel/command/AcceptCommand.java": "duel/command/AcceptCommand.java",
    "duel/command/CancelCommand.java": "duel/command/CancelCommand.java",
    "duel/command/ConfirmCommand.java": "duel/command/ConfirmCommand.java",
    "duel/command/CosmeticsCommand.java": "cosmetic/command/CosmeticsCommand.java",
    "duel/command/DenyCommand.java": "duel/command/DenyCommand.java",
    "duel/command/LeaveCommand.java": "duel/command/LeaveCommand.java",
    "duel/command/RewardsCommand.java": "reward/command/RewardsCommand.java",
    "duel/command/RootDuelCommand.java": "duel/command/RootDuelCommand.java",
    "duel/command/RootSpectateCommand.java": "duel/command/RootSpectateCommand.java",
    "duel/command/SpectateLeaveCommand.java": "duel/command/SpectateLeaveCommand.java",
    "duel/command/StatsCommand.java": "storage/command/StatsCommand.java",
    "duel/eventlisteners/BlockBreakListener.java": "listener/world/BlockBreakListener.java",
    "duel/eventlisteners/BlockPlaceListener.java": "listener/world/BlockPlaceListener.java",
    "duel/eventlisteners/CommandListener.java": "listener/player/CommandListener.java",
    "duel/eventlisteners/DamageEventListener.java": "listener/combat/DamageEventListener.java",
    "duel/eventlisteners/DeathEventListener.java": "listener/player/DeathEventListener.java",
    "duel/eventlisteners/EntityDamageByEntityEventListener.java": "listener/combat/EntityDamageByEntityEventListener.java",
    "duel/eventlisteners/EntityTagListener.java": "listener/visibility/EntityTagListener.java",
    "duel/eventlisteners/FireworkExplosionListener.java": "listener/combat/FireworkExplosionListener.java",
    "duel/eventlisteners/InteractEventListener.java": "listener/world/InteractEventListener.java",
    "duel/eventlisteners/JoinEventListener.java": "listener/player/JoinEventListener.java",
    "duel/eventlisteners/MoveEventListener.java": "listener/player/MoveEventListener.java",
    "duel/eventlisteners/PacketVisibilityListener.java": "listener/visibility/PacketVisibilityListener.java",
    "duel/eventlisteners/QuitEventListener.java": "listener/player/QuitEventListener.java",
    "duel/eventlisteners/RespawnEventListener.java": "listener/player/RespawnEventListener.java",
    "duel/playerdata/DatabaseManager.java": "storage/manager/DatabaseManager.java",
    "duel/playerdata/DataManager.java": "storage/manager/DataManager.java",
    "duel/playerdata/LocationData.java": "storage/model/LocationData.java",
    "duel/playerdata/PlayerData.java": "storage/model/PlayerData.java",
    "duel/playerdata/PlayerStats.java": "storage/model/PlayerStats.java",
    "duel/playerdata/StatsManager.java": "storage/manager/StatsManager.java",
    "duel/playerdata/adapter/ConfigurationSerializableAdapter.java": "storage/adapter/ConfigurationSerializableAdapter.java",
    "duel/playerdata/adapter/ItemStackAdapter.java": "storage/adapter/ItemStackAdapter.java",
    "duel/playerdata/adapter/ItemStackArrayAdapter.java": "storage/adapter/ItemStackArrayAdapter.java",
    "duel/request/Request.java": "duel/model/Request.java",
    "duel/request/RequestManager.java": "duel/manager/RequestManager.java",
    "duel/rewards/DuelReward.java": "reward/model/DuelReward.java",
    "duel/rewards/RewardManager.java": "reward/manager/RewardManager.java",
    "duel/rewards/RewardsGUI.java": "reward/gui/RewardsGUI.java",
    "kit/Kit.java": "kit/model/Kit.java",
    "kit/KitData.java": "kit/model/KitData.java",
    "kit/KitManager.java": "kit/manager/KitManager.java",
    "kit/KitSelectionGUI.java": "kit/gui/KitSelectionGUI.java",
    "kit/command/KitDeleteCommand.java": "kit/command/KitDeleteCommand.java",
    "kit/command/LoadKitCommand.java": "kit/command/LoadKitCommand.java",
    "kit/command/SaveKitCommand.java": "kit/command/SaveKitCommand.java",
    "party/Party.java": "party/model/Party.java",
    "party/PartyManager.java": "party/manager/PartyManager.java",
    "settings/Placeholder.java": "config/model/Placeholder.java",
    "settings/Setting.java": "config/model/Setting.java",
    "settings/Settings.java": "config/model/Settings.java",
    "settings/command/ConfigCommand.java": "config/command/ConfigCommand.java",
    "settings/command/ReloadCommand.java": "config/command/ReloadCommand.java",
    "settings/command/SaveCommand.java": "config/command/SaveCommand.java",
    "settings/command/SetCommand.java": "config/command/SetCommand.java",
    "settings/command/SupportCommand.java": "config/command/SupportCommand.java",
    "util/CustomPaginationBuilder.java": "util/CustomPaginationBuilder.java",
    "util/EffectUtil.java": "util/EffectUtil.java",
    "util/ItemSerializer.java": "util/ItemSerializer.java",
    "util/JsonUtil.java": "util/JsonUtil.java",
    "util/MenuManager.java": "util/MenuManager.java",
    "util/MenuUtil.java": "util/MenuUtil.java",
    "util/MessageUtil.java": "util/MessageUtil.java"
};

function getPackageFromPath(relPath) {
    const dirname = path.dirname(relPath);
    if (dirname === '.') return BASE_PKG;
    return BASE_PKG + '.' + dirname.replace(/\\/g, '/').replace(/\//g, '.');
}

const replacements = [];
for (const oldPath in MAPPING) {
    const newPath = MAPPING[oldPath];
    if (oldPath !== newPath) {
        const oldPkg = getPackageFromPath(oldPath);
        const newPkg = getPackageFromPath(newPath);
        const className = path.basename(oldPath, '.java');
        
        const oldFqn = oldPkg + '.' + className;
        const newFqn = newPkg + '.' + className;
        
        if (oldFqn !== newFqn) {
            replacements.push({ oldFqn, newFqn, len: oldFqn.length, className, newPkg });
        }
    }
}
replacements.sort((a, b) => b.len - a.len);

function getFiles(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        file = path.join(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            results = results.concat(getFiles(file));
        } else if (file.endsWith('.java')) {
            results.push(file);
        }
    });
    return results;
}

const allJavaFiles = getFiles(BASE_DIR);
const fileContents = {};

allJavaFiles.forEach(file => {
    let relPath = path.relative(BASE_DIR, file).replace(/\\/g, '/');
    let content = fs.readFileSync(file, 'utf8');

    // Remove BOM if present
    if (content.charCodeAt(0) === 0xFEFF) {
        content = content.substring(1);
    }
    
    // Replace Fully Qualified Names and basic usages
    replacements.forEach(rep => {
        // Replace exact FQN
        content = content.split(rep.oldFqn).join(rep.newFqn);
    });

    if (MAPPING[relPath]) {
        const newRel = MAPPING[relPath];
        const newPkg = getPackageFromPath(newRel);
        content = content.replace(/^package\s+[\w\.]+;/m, `package ${newPkg};`);
    }

    fileContents[relPath] = content;
});

// For each file, we need to add imports if it uses a class that moved to a different package,
// but was previously in the SAME package (so no import existed).
// This is complex, but we can just blindly add imports for any class that is mentioned in the file.
// Or we can rely on IDEs to fix imports, but since we are doing it via script:
// We'll scan for the class name. If it's used and not imported, and not in the same package, we add an import.

allJavaFiles.forEach(file => {
    let relPath = path.relative(BASE_DIR, file).replace(/\\/g, '/');
    let content = fileContents[relPath];
    let newRel = MAPPING[relPath] || relPath;
    let currentPkg = getPackageFromPath(newRel);
    
    let importsToAdd = [];
    replacements.forEach(rep => {
        if (rep.newPkg !== currentPkg) {
            // Check if className is used (as a whole word)
            const regex = new RegExp(`\\b${rep.className}\\b`);
            if (regex.test(content)) {
                // Check if it's already imported
                const importRegex = new RegExp(`import\\s+${rep.newFqn.replace(/\./g, '\\.')};`);
                if (!importRegex.test(content)) {
                    importsToAdd.push(`import ${rep.newFqn};`);
                }
            }
        }
    });
    
    if (importsToAdd.length > 0) {
        // Find the package declaration to insert after
        content = content.replace(/^(package\s+[\w\.]+;)/m, `$1\n\n${[...new Set(importsToAdd)].join('\n')}`);
    }
    fileContents[relPath] = content;
});

let movedCount = 0;
for (const oldRel in fileContents) {
    const newRel = MAPPING[oldRel] || oldRel;
    const newAbs = path.join(BASE_DIR, newRel);
    
    const dir = path.dirname(newAbs);
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
    
    fs.writeFileSync(newAbs, fileContents[oldRel], 'utf8');
    
    if (oldRel !== newRel) {
        const oldAbs = path.join(BASE_DIR, oldRel);
        if (fs.existsSync(oldAbs)) {
            fs.unlinkSync(oldAbs);
        }
        movedCount++;
    }
}

console.log(`Refactor complete. Moved ${movedCount} files and updated imports.`);

// Cleanup empty directories
function cleanEmptyDirs(dir) {
    if (!fs.existsSync(dir)) return;
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        const p = path.join(dir, file);
        if (fs.statSync(p).isDirectory()) {
            cleanEmptyDirs(p);
        }
    });
    if (fs.readdirSync(dir).length === 0) {
        fs.rmdirSync(dir);
    }
}
cleanEmptyDirs(BASE_DIR);
