package net.kyrptonaught.customportalapi.networking;

import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public record ForcePlacePortalPacket(BlockPos pos) {

    public static void sendForcePacket(ServerPlayerEntity player, BlockPos pos) {
        NetworkManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ForcePlacePortalPacket(pos));
    }

    public static ForcePlacePortalPacket decode(PacketByteBuf buf) {
        return new ForcePlacePortalPacket(buf.readBlockPos());
    }

    public static void encode(ForcePlacePortalPacket packet, PacketByteBuf buf) {
        buf.writeBlockPos(packet.pos);
    }

    public static void handle(ForcePlacePortalPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        MinecraftClient.getInstance().execute(() -> {
            World world = MinecraftClient.getInstance().world;
            BlockState oldState = world.getBlockState(packet.pos());
            world.setBlockState(packet.pos(), CustomPortalHelper.blockWithAxis(CustomPortalsMod.getDefaultPortalBlock().get().getDefaultState(), CustomPortalHelper.getAxisFrom(oldState)));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void register(SimpleChannel channel, Integer id) {
        channel.registerMessage(id, ForcePlacePortalPacket.class, ForcePlacePortalPacket::encode, ForcePlacePortalPacket::decode, ForcePlacePortalPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
