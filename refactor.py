import os
import re
import shutil

BASE_DIR = r"c:\Users\L900m\Downloads\Versus-main(1)\Versus-main\src\main\java\me\robomonkey\versus"
BASE_PKG = "me.robomonkey.versus"

MAPPING = {
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
}

def get_package_from_path(relative_path):
    dirname = os.path.dirname(relative_path)
    if dirname:
        return f"{BASE_PKG}.{dirname.replace('/', '.')}"
    return BASE_PKG

def build_import_replacements():
    replacements = []
    for old_path, new_path in MAPPING.items():
        if old_path == new_path:
            continue
        old_pkg = get_package_from_path(old_path)
        new_pkg = get_package_from_path(new_path)
        class_name = os.path.basename(old_path).replace(".java", "")
        
        old_fqn = f"{old_pkg}.{class_name}"
        new_fqn = f"{new_pkg}.{class_name}"
        
        if old_fqn != new_fqn:
            replacements.append((old_fqn, new_fqn))
    
    # Sort by length descending to prevent partial matches
    replacements.sort(key=lambda x: len(x[0]), reverse=True)
    return replacements

def main():
    import_replacements = build_import_replacements()
    
    # 1. Read all files and update their contents
    file_contents = {}
    for root, _, files in os.walk(BASE_DIR):
        for file in files:
            if not file.endswith(".java"):
                continue
            
            abs_path = os.path.join(root, file)
            # Find its key in mapping
            rel_path = os.path.relpath(abs_path, BASE_DIR).replace("\\", "/")
            
            with open(abs_path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            # Replace FQNs and imports
            for old_fqn, new_fqn in import_replacements:
                content = content.replace(old_fqn, new_fqn)
                
            # If the file itself is moving, update its package declaration
            if rel_path in MAPPING:
                new_rel = MAPPING[rel_path]
                new_pkg = get_package_from_path(new_rel)
                content = re.sub(r'^package\s+[\w\.]+;', f'package {new_pkg};', content, flags=re.MULTILINE)
            
            file_contents[rel_path] = content

    # 2. Write new files to the new locations
    moved_count = 0
    for old_rel, content in file_contents.items():
        new_rel = MAPPING.get(old_rel, old_rel)
        new_abs = os.path.join(BASE_DIR, new_rel.replace("/", os.sep))
        
        os.makedirs(os.path.dirname(new_abs), exist_ok=True)
        with open(new_abs, 'w', encoding='utf-8') as f:
            f.write(content)
            
        if old_rel != new_rel:
            old_abs = os.path.join(BASE_DIR, old_rel.replace("/", os.sep))
            if os.path.exists(old_abs):
                os.remove(old_abs)
            moved_count += 1
            
    print(f"Refactor complete. Moved {moved_count} files and updated imports.")
    
    # 3. Clean up empty directories
    for root, dirs, files in os.walk(BASE_DIR, topdown=False):
        for d in dirs:
            dir_path = os.path.join(root, d)
            try:
                os.rmdir(dir_path)
            except OSError:
                pass # not empty

if __name__ == "__main__":
    main()
