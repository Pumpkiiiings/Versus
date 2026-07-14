$BaseDir = "C:\Users\L900m\Downloads\Versus-main(1)\Versus-main\src\main\java\me\robomonkey\versus"
$BasePkg = "me.robomonkey.versus"

$Mapping = @{
    "RootVersusCommand.java" = "command/RootVersusCommand.java"
    "Versus.java" = "Versus.java"
    "arena/Arena.java" = "arena/model/Arena.java"
    "arena/ArenaBuilder.java" = "arena/editor/ArenaBuilder.java"
    "arena/ArenaBuilderCoordinator.java" = "arena/editor/ArenaBuilderCoordinator.java"
    "arena/ArenaEditor.java" = "arena/editor/ArenaEditor.java"
    "arena/ArenaKitsGUI.java" = "arena/gui/ArenaKitsGUI.java"
    "arena/ArenaManager.java" = "arena/manager/ArenaManager.java"
    "arena/ArenaProperty.java" = "arena/model/ArenaProperty.java"
    "arena/ArenaRollbackManager.java" = "arena/manager/ArenaRollbackManager.java"
    "arena/ArenaSelectionGUI.java" = "arena/gui/ArenaSelectionGUI.java"
    "arena/ArenaVisibilityManager.java" = "arena/manager/ArenaVisibilityManager.java"
    "arena/command/CreateCommand.java" = "arena/command/CreateCommand.java"
    "arena/command/DeleteCommand.java" = "arena/command/DeleteCommand.java"
    "arena/command/EditCommand.java" = "arena/command/EditCommand.java"
    "arena/command/ListCommand.java" = "arena/command/ListCommand.java"
    "arena/command/RootArenaCommand.java" = "arena/command/RootArenaCommand.java"
    "arena/command/SetCommand.java" = "arena/command/SetCommand.java"
    "arena/command/SetSpawnCommand.java" = "arena/command/SetSpawnCommand.java"
    "arena/command/VisitCommand.java" = "arena/command/VisitCommand.java"
    "command/AbstractCommand.java" = "command/AbstractCommand.java"
    "command/DuelGroupAccept.java" = "duel/command/DuelGroupAccept.java"
    "command/DuelGroupAcceptDuel.java" = "duel/command/DuelGroupAcceptDuel.java"
    "command/DuelGroupCommand.java" = "duel/command/DuelGroupCommand.java"
    "command/DuelGroupCreate.java" = "duel/command/DuelGroupCreate.java"
    "command/DuelGroupDecline.java" = "duel/command/DuelGroupDecline.java"
    "command/DuelGroupDeclineDuel.java" = "duel/command/DuelGroupDeclineDuel.java"
    "command/DuelGroupDisband.java" = "duel/command/DuelGroupDisband.java"
    "command/DuelGroupDuel.java" = "duel/command/DuelGroupDuel.java"
    "command/DuelGroupInvite.java" = "duel/command/DuelGroupInvite.java"
    "command/DuelGroupLeave.java" = "duel/command/DuelGroupLeave.java"
    "command/RootCommand.java" = "command/RootCommand.java"
    "cosmetics/CosmeticEffect.java" = "cosmetic/model/CosmeticEffect.java"
    "cosmetics/CosmeticsManager.java" = "cosmetic/manager/CosmeticsManager.java"
    "cosmetics/KillEffect.java" = "cosmetic/model/KillEffect.java"
    "cosmetics/VictoryEffect.java" = "cosmetic/model/VictoryEffect.java"
    "cosmetics/gui/CosmeticsMenu.java" = "cosmetic/gui/CosmeticsMenu.java"
    "cosmetics/gui/KillEffectsMenu.java" = "cosmetic/gui/KillEffectsMenu.java"
    "cosmetics/gui/VictoryEffectsMenu.java" = "cosmetic/gui/VictoryEffectsMenu.java"
    "cosmetics/killeffects/BloodKillEffect.java" = "cosmetic/model/killeffects/BloodKillEffect.java"
    "cosmetics/killeffects/ConfettiKillEffect.java" = "cosmetic/model/killeffects/ConfettiKillEffect.java"
    "cosmetics/killeffects/ExplosionKillEffect.java" = "cosmetic/model/killeffects/ExplosionKillEffect.java"
    "cosmetics/killeffects/LightningKillEffect.java" = "cosmetic/model/killeffects/LightningKillEffect.java"
    "cosmetics/killeffects/MagicKillEffect.java" = "cosmetic/model/killeffects/MagicKillEffect.java"
    "cosmetics/killeffects/NoneKillEffect.java" = "cosmetic/model/killeffects/NoneKillEffect.java"
    "cosmetics/killeffects/SmokeKillEffect.java" = "cosmetic/model/killeffects/SmokeKillEffect.java"
    "cosmetics/victoryeffects/DragonVictoryEffect.java" = "cosmetic/model/victoryeffects/DragonVictoryEffect.java"
    "cosmetics/victoryeffects/FireworksVictoryEffect.java" = "cosmetic/model/victoryeffects/FireworksVictoryEffect.java"
    "cosmetics/victoryeffects/FlamesVictoryEffect.java" = "cosmetic/model/victoryeffects/FlamesVictoryEffect.java"
    "cosmetics/victoryeffects/HeartsVictoryEffect.java" = "cosmetic/model/victoryeffects/HeartsVictoryEffect.java"
    "cosmetics/victoryeffects/MusicVictoryEffect.java" = "cosmetic/model/victoryeffects/MusicVictoryEffect.java"
    "cosmetics/victoryeffects/NoneVictoryEffect.java" = "cosmetic/model/victoryeffects/NoneVictoryEffect.java"
    "cosmetics/victoryeffects/SoulsVictoryEffect.java" = "cosmetic/model/victoryeffects/SoulsVictoryEffect.java"
    "dependency/Dependencies.java" = "dependency/Dependencies.java"
    "dependency/EconomyManager.java" = "dependency/EconomyManager.java"
    "dependency/ItemsAdderUtil.java" = "dependency/ItemsAdderUtil.java"
    "dependency/PAPIUtil.java" = "dependency/PAPIUtil.java"
    "dependency/VersusPlaceholderExpansion.java" = "dependency/VersusPlaceholderExpansion.java"
    "duel/BoundaryVisualizerTask.java" = "duel/task/BoundaryVisualizerTask.java"
    "duel/Countdown.java" = "duel/task/Countdown.java"
    "duel/Duel.java" = "duel/model/Duel.java"
    "duel/DuelManager.java" = "duel/manager/DuelManager.java"
    "duel/DuelState.java" = "duel/model/DuelState.java"
    "duel/ReturnOption.java" = "duel/model/ReturnOption.java"
    "duel/betting/BettingGUI.java" = "betting/gui/BettingGUI.java"
    "duel/betting/BettingManager.java" = "betting/manager/BettingManager.java"
    "duel/betting/BettingSession.java" = "betting/model/BettingSession.java"
    "duel/betting/RefundManager.java" = "betting/manager/RefundManager.java"
    "duel/command/AcceptCommand.java" = "duel/command/AcceptCommand.java"
    "duel/command/CancelCommand.java" = "duel/command/CancelCommand.java"
    "duel/command/ConfirmCommand.java" = "duel/command/ConfirmCommand.java"
    "duel/command/CosmeticsCommand.java" = "cosmetic/command/CosmeticsCommand.java"
    "duel/command/DenyCommand.java" = "duel/command/DenyCommand.java"
    "duel/command/LeaveCommand.java" = "duel/command/LeaveCommand.java"
    "duel/command/RewardsCommand.java" = "reward/command/RewardsCommand.java"
    "duel/command/RootDuelCommand.java" = "duel/command/RootDuelCommand.java"
    "duel/command/RootSpectateCommand.java" = "duel/command/RootSpectateCommand.java"
    "duel/command/SpectateLeaveCommand.java" = "duel/command/SpectateLeaveCommand.java"
    "duel/command/StatsCommand.java" = "storage/command/StatsCommand.java"
    "duel/eventlisteners/BlockBreakListener.java" = "listener/world/BlockBreakListener.java"
    "duel/eventlisteners/BlockPlaceListener.java" = "listener/world/BlockPlaceListener.java"
    "duel/eventlisteners/CommandListener.java" = "listener/player/CommandListener.java"
    "duel/eventlisteners/DamageEventListener.java" = "listener/combat/DamageEventListener.java"
    "duel/eventlisteners/DeathEventListener.java" = "listener/player/DeathEventListener.java"
    "duel/eventlisteners/EntityDamageByEntityEventListener.java" = "listener/combat/EntityDamageByEntityEventListener.java"
    "duel/eventlisteners/EntityTagListener.java" = "listener/visibility/EntityTagListener.java"
    "duel/eventlisteners/FireworkExplosionListener.java" = "listener/combat/FireworkExplosionListener.java"
    "duel/eventlisteners/InteractEventListener.java" = "listener/world/InteractEventListener.java"
    "duel/eventlisteners/JoinEventListener.java" = "listener/player/JoinEventListener.java"
    "duel/eventlisteners/MoveEventListener.java" = "listener/player/MoveEventListener.java"
    "duel/eventlisteners/PacketVisibilityListener.java" = "listener/visibility/PacketVisibilityListener.java"
    "duel/eventlisteners/QuitEventListener.java" = "listener/player/QuitEventListener.java"
    "duel/eventlisteners/RespawnEventListener.java" = "listener/player/RespawnEventListener.java"
    "duel/playerdata/DatabaseManager.java" = "storage/manager/DatabaseManager.java"
    "duel/playerdata/DataManager.java" = "storage/manager/DataManager.java"
    "duel/playerdata/LocationData.java" = "storage/model/LocationData.java"
    "duel/playerdata/PlayerData.java" = "storage/model/PlayerData.java"
    "duel/playerdata/PlayerStats.java" = "storage/model/PlayerStats.java"
    "duel/playerdata/StatsManager.java" = "storage/manager/StatsManager.java"
    "duel/playerdata/adapter/ConfigurationSerializableAdapter.java" = "storage/adapter/ConfigurationSerializableAdapter.java"
    "duel/playerdata/adapter/ItemStackAdapter.java" = "storage/adapter/ItemStackAdapter.java"
    "duel/playerdata/adapter/ItemStackArrayAdapter.java" = "storage/adapter/ItemStackArrayAdapter.java"
    "duel/request/Request.java" = "duel/model/Request.java"
    "duel/request/RequestManager.java" = "duel/manager/RequestManager.java"
    "duel/rewards/DuelReward.java" = "reward/model/DuelReward.java"
    "duel/rewards/RewardManager.java" = "reward/manager/RewardManager.java"
    "duel/rewards/RewardsGUI.java" = "reward/gui/RewardsGUI.java"
    "kit/Kit.java" = "kit/model/Kit.java"
    "kit/KitData.java" = "kit/model/KitData.java"
    "kit/KitManager.java" = "kit/manager/KitManager.java"
    "kit/KitSelectionGUI.java" = "kit/gui/KitSelectionGUI.java"
    "kit/command/KitDeleteCommand.java" = "kit/command/KitDeleteCommand.java"
    "kit/command/LoadKitCommand.java" = "kit/command/LoadKitCommand.java"
    "kit/command/SaveKitCommand.java" = "kit/command/SaveKitCommand.java"
    "party/Party.java" = "party/model/Party.java"
    "party/PartyManager.java" = "party/manager/PartyManager.java"
    "settings/Placeholder.java" = "config/model/Placeholder.java"
    "settings/Setting.java" = "config/model/Setting.java"
    "settings/Settings.java" = "config/model/Settings.java"
    "settings/command/ConfigCommand.java" = "config/command/ConfigCommand.java"
    "settings/command/ReloadCommand.java" = "config/command/ReloadCommand.java"
    "settings/command/SaveCommand.java" = "config/command/SaveCommand.java"
    "settings/command/SetCommand.java" = "config/command/SetCommand.java"
    "settings/command/SupportCommand.java" = "config/command/SupportCommand.java"
    "util/CustomPaginationBuilder.java" = "util/CustomPaginationBuilder.java"
    "util/EffectUtil.java" = "util/EffectUtil.java"
    "util/ItemSerializer.java" = "util/ItemSerializer.java"
    "util/JsonUtil.java" = "util/JsonUtil.java"
    "util/MenuManager.java" = "util/MenuManager.java"
    "util/MenuUtil.java" = "util/MenuUtil.java"
    "util/MessageUtil.java" = "util/MessageUtil.java"
}

function Get-PackageFromPath {
    param([string]$RelPath)
    $dirname = [System.IO.Path]::GetDirectoryName($RelPath)
    if ($dirname -eq "") {
        return $BasePkg
    }
    return "$BasePkg." + $dirname.Replace("\", "/").Replace("/", ".")
}

$Replacements = @()
foreach ($key in $Mapping.Keys) {
    $oldPath = $key
    $newPath = $Mapping[$key]
    
    if ($oldPath -ne $newPath) {
        $oldPkg = Get-PackageFromPath $oldPath
        $newPkg = Get-PackageFromPath $newPath
        
        $className = [System.IO.Path]::GetFileNameWithoutExtension($oldPath)
        
        $oldFqn = "$oldPkg.$className"
        $newFqn = "$newPkg.$className"
        
        if ($oldFqn -ne $newFqn) {
            $Replacements += [PSCustomObject]@{ OldFqn = $oldFqn; NewFqn = $newFqn; Length = $oldFqn.Length }
        }
    }
}
$Replacements = $Replacements | Sort-Object Length -Descending

$FileContents = @{}
$files = Get-ChildItem -Path $BaseDir -Recurse -Filter *.java
foreach ($file in $files) {
    $absPath = $file.FullName
    $relPath = $absPath.Substring($BaseDir.Length + 1).Replace("\", "/")
    
    $content = [System.IO.File]::ReadAllText($absPath, [System.Text.Encoding]::UTF8)
    
    foreach ($rep in $Replacements) {
        $content = $content.Replace($rep.OldFqn, $rep.NewFqn)
    }
    
    if ($Mapping.ContainsKey($relPath)) {
        $newRel = $Mapping[$relPath]
        $newPkg = Get-PackageFromPath $newRel
        $content = [System.Text.RegularExpressions.Regex]::Replace($content, "(?m)^package\s+[\w\.]+;", "package $newPkg;")
    }
    
    $FileContents[$relPath] = $content
}

$movedCount = 0
foreach ($oldRel in $FileContents.Keys) {
    $newRel = $oldRel
    if ($Mapping.ContainsKey($oldRel)) {
        $newRel = $Mapping[$oldRel]
    }
    
    $newAbs = [System.IO.Path]::Combine($BaseDir, $newRel.Replace("/", [System.IO.Path]::DirectorySeparatorChar.ToString()))
    $dir = [System.IO.Path]::GetDirectoryName($newAbs)
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Force -Path $dir | Out-Null
    }
    
    [System.IO.File]::WriteAllText($newAbs, $FileContents[$oldRel], [System.Text.Encoding]::UTF8)
    
    if ($oldRel -ne $newRel) {
        $oldAbs = [System.IO.Path]::Combine($BaseDir, $oldRel.Replace("/", [System.IO.Path]::DirectorySeparatorChar.ToString()))
        if (Test-Path $oldAbs) {
            Remove-Item -Path $oldAbs -Force
        }
        $movedCount++
    }
}

Write-Host "Refactor complete. Moved $movedCount files and updated imports."
