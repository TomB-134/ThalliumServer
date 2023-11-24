package net.thallium.commands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandFindBlock extends CommandThalliumBase {
    @Override
    public String getName() {
        return "findBlock";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "findBlock block";
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] args) throws CommandException {
        EntityPlayerMP playerMP = getCommandSenderAsPlayer(iCommandSender);
        BlockPos playerPosition = playerMP.getPosition();
        Block blockToFind = CommandBase.getBlockByText(iCommandSender, args[0]);

        Chunk chunkToSearch = playerMP.getServerWorld().getChunk(playerPosition);
        ArrayList<BlockPos> targetBlocks = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    IBlockState state = chunkToSearch.getBlockState(new BlockPos(x,y,z));
                    if (state.getBlock() == blockToFind) {
                        targetBlocks.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        if (targetBlocks.isEmpty()) {
            iCommandSender.sendMessage(new TextComponentString("No target blocks found in chunk (" + chunkToSearch.x + ", " + chunkToSearch.z + ")"));
        } else {
            iCommandSender.sendMessage(new TextComponentString(targetBlocks.size() + " target blocks found in chunk (" + chunkToSearch.x + ", " + chunkToSearch.z + ")"));
            for (BlockPos pos : targetBlocks) {
                iCommandSender.sendMessage(new TextComponentString("x" + (pos.getX() + (chunkToSearch.x * 16)) + " y" + pos.getY() + " z" + (pos.getZ() + (chunkToSearch.z * 16))));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer p_getTabCompletions_1_, ICommandSender p_getTabCompletions_2_, String[] p_getTabCompletions_3_, @Nullable BlockPos p_getTabCompletions_4_) {
        return getListOfStringsMatchingLastWord(p_getTabCompletions_3_, Block.REGISTRY.getKeys());
    }
}
