package net.kyrptonaught.customportalapi.portal;

import net.kyrptonaught.customportalapi.mixin.BucketMixin;
import net.kyrptonaught.customportalapi.util.CustomPortalFluidProvider;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;

public class PortalIgnitionSource {
    public final static PortalIgnitionSource FIRE = new PortalIgnitionSource(SourceType.BLOCKPLACED, Registry.BLOCK.getId(Blocks.FIRE));
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

    public static PortalIgnitionSource ItemUseSource(Item item) {
        USEITEMS.add(item);
        if (item instanceof CustomPortalFluidProvider)
            return new PortalIgnitionSource(SourceType.FLUID, Registry.FLUID.getId(((CustomPortalFluidProvider) item).getFluidContent()));
        return new PortalIgnitionSource(SourceType.USEITEM, Registry.ITEM.getId(item));
    }

    public static PortalIgnitionSource FluidSource(Fluid fluid) {
        return new PortalIgnitionSource(SourceType.FLUID, Registry.FLUID.getId(fluid));
    }

    public static PortalIgnitionSource CustomSource(Identifier ignitionSourceID) {
        return new PortalIgnitionSource(SourceType.CUSTOM, ignitionSourceID);
    }

    public boolean isWater() {
        return FluidTags.WATER.contains(Registry.FLUID.get(ignitionSourceID));
    }

    public boolean isLava() {
        return FluidTags.LAVA.contains(Registry.FLUID.get(ignitionSourceID));
    }

    public static boolean isRegisteredIgnitionSourceWith(Item item) {
        return item instanceof BucketItem || USEITEMS.contains(item);
    }
}