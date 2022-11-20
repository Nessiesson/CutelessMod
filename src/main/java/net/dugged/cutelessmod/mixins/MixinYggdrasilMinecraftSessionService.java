package net.dugged.cutelessmod.mixins;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.Response;
import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.SelfhostedYggrasilRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = YggdrasilMinecraftSessionService.class, remap = false)
public abstract class MixinYggdrasilMinecraftSessionService {
	@Shadow
	public YggdrasilAuthenticationService getAuthenticationService() {
		return null;
	}

	@Shadow
	@Final
	private Gson gson;
	@Shadow
	@Final
	private static URL JOIN_URL;

	private static final Pattern privateKeyPattern = Pattern.compile("(?<=-----BEGIN OPENSSH PRIVATE KEY-----).*(?=-----END OPENSSH PRIVATE KEY-----)", Pattern.DOTALL);

	@Inject(method = "joinServer", at = @At(value = "HEAD", target = "Lcom/mojang/authlib/yggdrasil/request/JoinMinecraftServerRequest;<init>()V"), cancellable = true)
	private void selfhostedYggdrasil(GameProfile profile, String authenticationToken, String serverId, CallbackInfo ci) throws AuthenticationException, IOException {
		if (Configuration.selfhostedYggdrasil) {
			SelfhostedYggrasilRequest request = new SelfhostedYggrasilRequest();
			request.accessToken = authenticationToken;
			request.selectedProfile = profile.getId();
			request.serverId = serverId;
			String authString = Configuration.selfhostedYggdrasilKey;
			if (!Configuration.selfhostedYggdrasilKey.isEmpty()) {
				File f = new File(Configuration.selfhostedYggdrasilKey.replace("~", System.getProperty("user.home")));
				if (f.exists() && !f.isDirectory()) {
					String file = FileUtils.readFileToString(f, Charset.defaultCharset()).trim();
					Matcher matcher = privateKeyPattern.matcher(file);
					if (matcher.find()) {
						authString = DigestUtils.sha1Hex(matcher.group(0));
					} else {
						authString = file;
					}
				}
			}
			request.authString = authString;
			makeRequest(JOIN_URL, request, Response.class);
			ci.cancel();
		}
	}

	// Copied method to hack-fix around protected class
	protected <T extends Response> T makeRequest(URL url, Object input, Class<T> classOfT) throws AuthenticationException {
		try {
			String jsonResult = input == null ? getAuthenticationService().performGetRequest(url) : getAuthenticationService().performPostRequest(url, gson.toJson(input), "application/json");
			T result = gson.fromJson(jsonResult, classOfT);
			if (result == null) {
				return null;
			} else if (StringUtils.isNotBlank(result.getError())) {
				if ("UserMigratedException".equals(result.getCause())) {
					throw new UserMigratedException(result.getErrorMessage());
				} else if ("ForbiddenOperationException".equals(result.getError())) {
					throw new InvalidCredentialsException(result.getErrorMessage());
				} else {
					throw new AuthenticationException(result.getErrorMessage());
				}
			} else {
				return result;
			}
		} catch (IOException var6) {
			throw new AuthenticationUnavailableException("Cannot contact authentication server", var6);
		} catch (IllegalStateException var7) {
			throw new AuthenticationUnavailableException("Cannot contact authentication server", var7);
		} catch (JsonParseException var8) {
			throw new AuthenticationUnavailableException("Cannot contact authentication server", var8);
		}
	}
}
