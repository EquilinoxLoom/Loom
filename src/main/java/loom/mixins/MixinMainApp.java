package loom.mixins;

import blueprints.Blueprint;
import gameManaging.GameManager;
import main.MainApp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import resourceManagement.BlueprintRepository;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(value = {MainApp.class}, remap = false)
public abstract class MixinMainApp {
	@Shadow private static void cheat() {}

	@Unique private static <T> void loom$print(Predicate<Blueprint> predicate, Function<Blueprint, String> func) {
		BlueprintRepository.getAllBlueprints().stream().filter(predicate).map(func).forEach(System.out::println);
		System.exit(0);
	}

	@Unique private static void loom$cheat() {
		cheat(); GameManager.getSession().getStats().increaseDp(1000000000);
	}

	/*
	@Inject(method = "main", at = @At(value = "INVOKE", target = "LgameManaging/GameManager;init()V", shift = At.Shift.AFTER))
	private static void print(String[] args, CallbackInfo ci) {
		GameText.init(0);
		loom$print(blueprint -> blueprint.getComponent(ComponentType.INFO) != null, blueprint -> {
			StringBuilder builder = new StringBuilder();
			InformationComponent.InformationCompBlueprint info = ((InformationComponent.InformationCompBlueprint) blueprint.getComponent(ComponentType.INFO));
			builder.append(info.getName()).append("\n\tprice: ").append(info.getPrice());
			LifeCompBlueprint life = (LifeCompBlueprint) blueprint.getComponent(ComponentType.LIFE);
			if (life != null) {
				try {
					Field field = life.getClass().getDeclaredField("averageLifeLength");
					field.setAccessible(true);
					builder.append("\n\tlifespan: ").append(field.get(life));
				} catch (NoSuchFieldException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			MaterialComponent.MaterialCompBlueprint material = (MaterialComponent.MaterialCompBlueprint) blueprint.getComponent(ComponentType.MATERIAL);
			if (material != null) {
				builder.append("\n\tcolors: ");
				for (NaturalColour colour : material.getTrait().getNaturalColours()) {
					builder.append("\n\t\t").append(colour.name).append(" ").append(colour.price);
				}
			}
			return builder.toString();
        });
	}
	 */
}
