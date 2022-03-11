package net.kyrptonaught.customportalapi.client;

import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.launcher.SessionEventListener;
import com.mojang.serialization.Codec;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.PerWorldPortals;
import net.kyrptonaught.customportalapi.mixin.client.ChunkRendererRegionAccessor;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@OnlyIn(Dist.CLIENT)
public class CustomPortalsModClient {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CustomPortalsMod.MOD_ID);


    public static final RegistryObject<ParticleType<BlockStateParticleEffect>> CUSTOMPORTALPARTICLE = PARTICLES.register("customportalparticle", () -> new ParticleType<BlockStateParticleEffect>(false, BlockStateParticleEffect.PARAMETERS_FACTORY) {
        private Codec<BlockStateParticleEffect> codec = BlockStateParticleEffect.createCodec(this);
        @Override
        public Codec<BlockStateParticleEffect> getCodec() {
            return codec;
        }
    });

    private static void onBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerColorProvider((state, world, pos, tintIndex) -> {
            if (pos != null && world instanceof ChunkRendererRegion) {
                Block block = CustomPortalHelper.getPortalBase(((ChunkRendererRegionAccessor) world).getWorld(), pos);
                PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(block);
                if (link != null) return link.colorID;
            }
            return 1908001;
        }, CustomPortalsMod.portalBlock.get());
    }

    public static void onInitializeClient(IEventBus bus) {
        PARTICLES.register(bus);
        bus.addListener(CustomPortalsModClient::onBlockColors);
        bus.addListener(CustomPortalsModClient::onClientSetup);

        bus.addListener(CustomPortalsModClient::onParticleFactoryRegistry);
    }

    public static void onParticleFactoryRegistry(final ParticleFactoryRegisterEvent event) {
        MinecraftClient.getInstance().particleManager.registerFactory(CUSTOMPORTALPARTICLE.get(), CustomPortalParticle.Factory::new);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> RenderLayers.setRenderLayer(CustomPortalsMod.portalBlock.get(), RenderLayer.getTranslucent()));

        MinecraftClient.getInstance().getGame().setSessionEventListener(new SessionEventListener() {
            @Override
            public void onStartGameSession(GameSession session) {
            }

            @Override
            public void onLeaveGameSession(GameSession session) {
                PerWorldPortals.removeOldPortalsFromRegistry();
            }
        });
    }
}