package com.miaokatze.gtit.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.miaokatze.gtit.common.util.GTITUtils;
import com.miaokatze.gtit.register.CreativeTabManager;

/**
 * 高阶猫猫币（第 3–10 档通用实现）
 * <p>
 * 星光 / 月华 / 曜阳 / 翡翠 / 湛蓝 / 紫曜 / 虹彩 / 永恒猫猫币共用此实现，
 * 仅靠构造入参区分注册名（unlocalized）与贴图名，避免为每档各写一个类。
 * 与 {@link NekoCoin} 风格一致：创造栏、堆叠上限 64、lang key 逐行 tooltip。
 */
public class NekoTierCoin extends Item {

    /** 未本地化名（如 "starlight_neko_coin"），tooltip lang key 前缀据此拼装 */
    private final String unlocalizedName;

    /**
     * 构造高阶猫猫币
     *
     * @param unlocalizedName 物品未本地化名（= 注册名，如 "starlight_neko_coin"）
     * @param textureName     贴图资源名（如 "gtit:miao_coin_starlight"）
     */
    public NekoTierCoin(String unlocalizedName, String textureName) {
        super();
        this.unlocalizedName = unlocalizedName;
        setUnlocalizedName(unlocalizedName);
        setTextureName(textureName);
        setCreativeTab(CreativeTabManager.CREATIVE_TAB);
        setMaxStackSize(64);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean showAdvanced) {
        for (int i = 0;; i++) {
            String key = "item." + unlocalizedName + ".tooltip." + i;
            String line = StatCollector.translateToLocal(key);
            if (line.equals(key)) break;
            tooltip.add(EnumChatFormatting.YELLOW + line);
        }
        tooltip.add(GTITUtils.getAddedByLine());
    }
}
