package com.example.examplemod;

import net.minecraft.data.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleDataGeneration {

    @SubscribeEvent
    public static void onDataGeneration(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeClient()) {
            generator.addProvider(new Lang(generator));
            generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            BlockTags blockTags = new BlockTags(generator, event.getExistingFileHelper());

            generator.addProvider(blockTags);
            generator.addProvider(new ItemTags(generator, blockTags, event.getExistingFileHelper()));
            generator.addProvider(new Recipes(generator));
        }
    }

    private static class Lang extends LanguageProvider {
        public Lang(DataGenerator generator) {
            super(generator, ExampleMod.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(ExampleMod.DeferredRegistries.EXAMPLE_ITEM.get(), "Example Item");

            add("itemGroup." + ExampleMod.MOD_ID, ExampleMod.MOD_NAME);
        }
    }

    private static class ItemModels extends ItemModelProvider {
        public ItemModels(DataGenerator generator, ExistingFileHelper helper) {
            super(generator, ExampleMod.MOD_ID, helper);
        }

        @Override
        protected void registerModels() {
            basicIcon(ExampleMod.DeferredRegistries.EXAMPLE_ITEM.getId());
        }

        private void basicIcon(ResourceLocation item) {
            getBuilder(item.getPath())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", new ResourceLocation(ExampleMod.MOD_ID, "item/" + item.getPath()));
        }
    }

    private static class Recipes extends RecipeProvider {
        public Recipes(DataGenerator generator) {
            super(generator);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder.shapedRecipe(ExampleMod.DeferredRegistries.EXAMPLE_ITEM.get(), 2)
                    .patternLine("X ")
                    .patternLine(" X")
                    .key('X', Ingredient.fromTag(Tags.Items.RODS_WOODEN))
                    .addCriterion("has_stick", hasItem(Tags.Items.RODS_WOODEN))
                    .build(consumer);
        }
    }

    private static class ItemTags extends ItemTagsProvider {

        public ItemTags(DataGenerator generator, BlockTags blockTags, ExistingFileHelper helper) {
            super(generator, blockTags, ExampleMod.MOD_ID, helper);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(Tags.Items.RODS_WOODEN).add(ExampleMod.DeferredRegistries.EXAMPLE_ITEM.get());
        }
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
            super(generator, ExampleMod.MOD_ID, helper);
        }

        @Override
        protected void registerTags() {

        }
    }

}
