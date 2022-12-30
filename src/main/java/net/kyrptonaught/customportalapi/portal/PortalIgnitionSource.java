package net.kyrptonaught.customportalapi.portal;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiFunction;

public class PortalIgnitionSource {
    @SuppressWarnings("deprecation")
	public final static PortalIgnitionSource FIRE = new PortalIgnitionSource(SourceType.BLOCKPLACED, Registries.BLOCK.getId(Blocks.FIRE));
    public final static PortalIgnitionSource WATER = FluidSource(Fluids.WATER);

    public enum SourceType {
        USEITEM, BLOCKPLACED, FLUID, CUSTOM
    }

    private static final HashSet<Item> USEITEMS = new HashSet<>();
    public SourceType sourceType;
    public Identifier ignitionSourceID;

    private PortalIgnitionSource(SourceType sourceType, Identifier ignitionSourceID) {
        this.sourceType = sourceType;
        this.ignitionSourceID = ignitionSourceID;
    }

    @SuppressWarnings("deprecation")
	public static PortalIgnitionSource ItemUseSource(Item item) {
        USEITEMS.add(item);
        return new PortalIgnitionSource(SourceType.USEITEM, Registries.ITEM.getId(item));
    }

    @SuppressWarnings("deprecation")
	public static PortalIgnitionSource FluidSource(Fluid fluid) {
        return new PortalIgnitionSource(SourceType.FLUID, Registries.FLUID.getId(fluid));
    }

    public static PortalIgnitionSource CustomSource(Identifier ignitionSourceID) {
        return new PortalIgnitionSource(SourceType.CUSTOM, ignitionSourceID);
    }

    // TODO: implement
    @Deprecated
    public void withCondition(BiFunction<World, BlockPos, Boolean> condition) {

    }

    @SuppressWarnings("deprecation")
	public boolean isWater() {
        return Optional.ofNullable(ForgeRegistries.FLUIDS.getValue(ignitionSourceID)).filter(a -> a.isIn(FluidTags.WATER)).isPresent();
    }

    @SuppressWarnings("deprecation")
	public boolean isLava() {
        return Optional.ofNullable(ForgeRegistries.FLUIDS.getValue(ignitionSourceID)).filter(a -> a.isIn(FluidTags.LAVA)).isPresent();
    }

    public static boolean isRegisteredIgnitionSourceWith(Item item) {
        return USEITEMS.contains(item);
    }
}
