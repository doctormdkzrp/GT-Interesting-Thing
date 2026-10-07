package com.miaokatze.gtit.currency;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.miaokatze.gtit.common.api.enums.GTITItemList;

/**
 * 猫猫币货币注册表
 * 定义猫猫币的 ID、显示名称、关联物品等
 * 完全独立于 VM 的 CurrencyType 枚举系统
 * <p>
 * O2-B03：自 trade 包上提共享内核——「猫猫币」是 mail/lottery/signin/machine.v2/client.gui
 * 五域共用的全仓词汇（ID 常量/物品引用/格式化/解析），住在 trade 域会让纯消费域产生
 * 词汇性依赖，掩盖真正的业务耦合热点；init 时机不变（CommonProxy.postInit 调用）。
 * <p>
 * 任务二：自 2 种扩展为 10 种，沿同一条递归进制链（第 N+1 档 = 第 N 档 × 64，反向 56 回收）。
 * 由「两个静态字段 + 硬编码 if」改造为表驱动通用实现，新增档位只需往常量与表里各加一行。
 */
public class NekoCurrencyRegistrar {

    /** 统一 logger（O2-B02 去中心化：与主类同用 "gtit" logger 名，日志过滤口径不变） */
    private static final Logger LOG = LogManager.getLogger("gtit");

    // ==================== 档位进制常量 ====================

    /** 相邻档位兑换率：高档 1 枚 = 低档 64 枚 */
    public static final int TIER_RATE = 64;
    /** 反向兑换回收率：高档 1 枚 → 低档 56 枚（差额 8 枚为铸币税，与既有 64/56 口径一致） */
    public static final int TIER_BUYBACK = 56;

    // ==================== 猫猫币 ID 常量（档位 1→10 升序） ====================

    public static final String NEKO_ID = "neko";
    public static final String SHIMMERING_NEKO_ID = "shimmeringNeko";
    public static final String STARLIGHT_NEKO_ID = "starlightNeko";
    public static final String MOONLIT_NEKO_ID = "moonlitNeko";
    public static final String SUNFIRE_NEKO_ID = "sunfireNeko";
    public static final String JADE_NEKO_ID = "jadeNeko";
    public static final String SAPPHIRE_NEKO_ID = "sapphireNeko";
    public static final String AMETHYST_NEKO_ID = "amethystNeko";
    public static final String PRISMATIC_NEKO_ID = "prismaticNeko";
    public static final String ETERNAL_NEKO_ID = "eternalNeko";

    // ==================== 猫猫币物品引用 ====================

    /** 前两档旧字段保留（兼容既有引用，勿删/勿改名） */
    public static Item nekoCoinItem = null;
    public static Item shimmeringNekoCoinItem = null;

    /** 货币 ID 有序数组（按档次升序 = {@link #getNekoCurrencyIds()} 的顺序，UI 依赖此顺序） */
    private static final String[] TIERED_IDS = new String[] { NEKO_ID, SHIMMERING_NEKO_ID, STARLIGHT_NEKO_ID,
        MOONLIT_NEKO_ID, SUNFIRE_NEKO_ID, JADE_NEKO_ID, SAPPHIRE_NEKO_ID, AMETHYST_NEKO_ID, PRISMATIC_NEKO_ID,
        ETERNAL_NEKO_ID };

    /** ID → 物品 映射（init 时填充） */
    private static final Map<String, Item> ID_TO_ITEM = new LinkedHashMap<>();
    /** 物品 → ID 映射（init 时填充，用于物品栈反向识别） */
    private static final Map<Item, String> ITEM_TO_ID = new LinkedHashMap<>();

    /** ID → 中文显示名（静态初始化，无需等待 init） */
    private static final Map<String, String> DISPLAY_NAMES = new LinkedHashMap<>();

    static {
        DISPLAY_NAMES.put(NEKO_ID, "猫猫币");
        DISPLAY_NAMES.put(SHIMMERING_NEKO_ID, "闪烁猫猫币");
        DISPLAY_NAMES.put(STARLIGHT_NEKO_ID, "星光猫猫币");
        DISPLAY_NAMES.put(MOONLIT_NEKO_ID, "月华猫猫币");
        DISPLAY_NAMES.put(SUNFIRE_NEKO_ID, "曜阳猫猫币");
        DISPLAY_NAMES.put(JADE_NEKO_ID, "翡翠猫猫币");
        DISPLAY_NAMES.put(SAPPHIRE_NEKO_ID, "湛蓝猫猫币");
        DISPLAY_NAMES.put(AMETHYST_NEKO_ID, "紫曜猫猫币");
        DISPLAY_NAMES.put(PRISMATIC_NEKO_ID, "虹彩猫猫币");
        DISPLAY_NAMES.put(ETERNAL_NEKO_ID, "永恒猫猫币");
    }

    /**
     * 初始化猫猫币注册
     * 在 CommonProxy.postInit 中调用（确保物品已注册）
     */
    public static void init() {
        ID_TO_ITEM.clear();
        ITEM_TO_ID.clear();
        register(NEKO_ID, GTITItemList.NekoCoin);
        register(SHIMMERING_NEKO_ID, GTITItemList.ShimmeringNekoCoin);
        register(STARLIGHT_NEKO_ID, GTITItemList.StarlightNekoCoin);
        register(MOONLIT_NEKO_ID, GTITItemList.MoonlitNekoCoin);
        register(SUNFIRE_NEKO_ID, GTITItemList.SunfireNekoCoin);
        register(JADE_NEKO_ID, GTITItemList.JadeNekoCoin);
        register(SAPPHIRE_NEKO_ID, GTITItemList.SapphireNekoCoin);
        register(AMETHYST_NEKO_ID, GTITItemList.AmethystNekoCoin);
        register(PRISMATIC_NEKO_ID, GTITItemList.PrismaticNekoCoin);
        register(ETERNAL_NEKO_ID, GTITItemList.EternalNekoCoin);

        nekoCoinItem = ID_TO_ITEM.get(NEKO_ID);
        shimmeringNekoCoinItem = ID_TO_ITEM.get(SHIMMERING_NEKO_ID);

        LOG.info(
            "猫猫币注册完成: neko={}, shimmeringNeko={}, starlightNeko={}, moonlitNeko={}, sunfireNeko={}, "
                + "jadeNeko={}, sapphireNeko={}, amethystNeko={}, prismaticNeko={}, eternalNeko={}",
            nekoCoinItem,
            shimmeringNekoCoinItem,
            ID_TO_ITEM.get(STARLIGHT_NEKO_ID),
            ID_TO_ITEM.get(MOONLIT_NEKO_ID),
            ID_TO_ITEM.get(SUNFIRE_NEKO_ID),
            ID_TO_ITEM.get(JADE_NEKO_ID),
            ID_TO_ITEM.get(SAPPHIRE_NEKO_ID),
            ID_TO_ITEM.get(AMETHYST_NEKO_ID),
            ID_TO_ITEM.get(PRISMATIC_NEKO_ID),
            ID_TO_ITEM.get(ETERNAL_NEKO_ID));
    }

    /** 把一档货币的 ID 与物品填进双向表 */
    private static void register(String id, GTITItemList list) {
        Item item = list.getItem();
        ID_TO_ITEM.put(id, item);
        ITEM_TO_ID.put(item, id);
    }

    /**
     * 获取所有猫猫币 ID（按档位升序）
     */
    public static String[] getNekoCurrencyIds() {
        return TIERED_IDS.clone();
    }

    /**
     * 获取猫猫币的档位（1..10），未知 ID 返回 -1
     */
    public static int getTier(String currencyId) {
        if (currencyId == null) return -1;
        for (int i = 0; i < TIERED_IDS.length; i++) {
            if (TIERED_IDS[i].equals(currencyId)) return i + 1;
        }
        return -1;
    }

    /**
     * 获取猫猫币的显示名称
     */
    public static String getDisplayName(String currencyId) {
        String name = DISPLAY_NAMES.get(currencyId);
        return name == null ? "未知货币" : name;
    }

    /**
     * 判断物品是否是猫猫币
     * <p>
     * 查双向表之外仍认前两档的旧静态字段：那两枚字段是 public 的历史契约（零依赖测试用它打桩
     * 「币物品已注册」），init 之前/测试环境下必须继续生效，否则既有的贸易/口袋回归套件会失效。
     */
    public static boolean isNekoCoinItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        return getNekoCurrencyId(stack) != null;
    }

    /**
     * 从物品堆栈获取猫猫币 ID
     * <p>
     * 优先查表（覆盖 10 档）；表未命中时回退到前两档的旧静态字段（见
     * {@link #isNekoCoinItem(ItemStack)} 的兼容说明）。
     *
     * @return 猫猫币 ID，如果不是猫猫币返回 null
     */
    public static String getNekoCurrencyId(ItemStack stack) {
        if (stack == null) return null;
        Item item = stack.getItem();
        if (item == null) return null;
        // 旧静态字段优先（前两档的历史契约；init 后与表内容一致，无行为分歧）
        if (item == nekoCoinItem) return NEKO_ID;
        if (item == shimmeringNekoCoinItem) return SHIMMERING_NEKO_ID;
        return ITEM_TO_ID.get(item);
    }

    /**
     * 从猫猫币 ID 获取物品堆栈
     * <p>
     * 优先查表（覆盖 10 档）；表未命中时回退到前两档的旧静态字段（测试桩场景）。
     */
    public static ItemStack getItemStack(String currencyId, int amount) {
        Item item = ID_TO_ITEM.get(currencyId);
        if (item == null) {
            if (NEKO_ID.equals(currencyId)) item = nekoCoinItem;
            else if (SHIMMERING_NEKO_ID.equals(currencyId)) item = shimmeringNekoCoinItem;
        }
        if (item == null) return null;
        return new ItemStack(item, amount);
    }
}
