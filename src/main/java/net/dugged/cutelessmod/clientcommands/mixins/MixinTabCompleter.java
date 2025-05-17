package net.dugged.cutelessmod.clientcommands.mixins;


import com.google.common.collect.ObjectArrays;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabCompleter.class)
public abstract class MixinTabCompleter {

	@ModifyArg(method = "complete", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;writeText(Ljava/lang/String;)V"))
	private String stripFormattingCodes(String text) {
		return TextFormatting.getTextWithoutFormattingCodes(text);
	}

	@Inject(method = "requestCompletions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V"))
	private void addAutoComplete(String prefix, CallbackInfo ci) {
		ClientCommandHandler.getInstance().autoComplete(prefix);
	}

	@ModifyVariable(method = "setCompletions", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", remap = false), argsOnly = true)
	private String[] setLatestAutoComplete(String... completions) {
		final String[] complete = ClientCommandHandler.getInstance().latestAutoComplete;
		if (complete != null) {
			return ObjectArrays.concat(complete, completions, String.class);
		}
		return completions;
	}

	@Redirect(method = "setCompletions", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;getCommonPrefix([Ljava/lang/String;)Ljava/lang/String;", remap = false))
	private String adjustedGetCommonPrefix(String[] strings) {
		final String string = StringUtils.getCommonPrefix(strings);
		return TextFormatting.getTextWithoutFormattingCodes(string);
	}
}