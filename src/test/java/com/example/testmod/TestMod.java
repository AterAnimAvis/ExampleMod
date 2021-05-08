package com.example.testmod;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod("test_mod")
public class TestMod {

    public TestMod() {
        System.out.println(ModList.get().isLoaded("test_mod"));
        System.out.println(ModList.get().isLoaded("example_mod"));
    }

}
