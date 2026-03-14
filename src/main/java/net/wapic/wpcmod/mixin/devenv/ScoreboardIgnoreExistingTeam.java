package net.wapic.wpcmod.mixin.devenv;

import com.moulberry.mixinconstraints.annotations.IfDevEnvironment;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfDevEnvironment
@Mixin(Scoreboard.class)
public class ScoreboardIgnoreExistingTeam {
	@Redirect(method = "addPlayerTeam(Ljava/lang/String;)Lnet/minecraft/world/scores/PlayerTeam;", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
	public void onExistingteam(Logger instance, String s, Object o) {
		// Ignore creations of existing teams
	}
}
