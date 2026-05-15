#!/bin/bash
# 下载猫粮品牌真实产品图片到 uploads/ 目录
cd /Users/zhaomu/IdeaProjects/catplanet/catplanet-server/uploads

echo "=== 开始下载猫粮产品真实图片 ==="

# 2001 - 渴望六种鱼 (Orijen Six Fish) - 官网
echo "[1/23] 渴望六种鱼..."
curl -sL -o food-orijen-6fish.png "https://apac.orijenpetfoods.com/dw/image/v2/BFDW_PRD/on/demandware.static/-/Sites-orijen-ap-master-catalog/default/dwed49fb84/2023/Cat-2023/SIX%20FISH/ORIJEN%20%20Six%20Fish%20Cat%20Front%20Right%205.4kg%20APAC.png?sw=450"

# 2002 - 渴望鸡肉 (Orijen Original Cat) - 官网
echo "[2/23] 渴望鸡肉..."
curl -sL -o food-orijen-chicken.png "https://apac.orijenpetfoods.com/dw/image/v2/BFDW_PRD/on/demandware.static/-/Sites-orijen-ap-master-catalog/default/dweca1a8dd/2023/Cat-2023/ORIGINAL%20CAT/ORIJEN%20Original%20Cat%20Front%20Right%205.4kg%20APAC.png?sw=450"

# 2003 - 爱肯拿农场盛宴 (Acana Wild Prairie) - 官网
echo "[3/23] 爱肯拿农场盛宴..."
curl -sL -o food-acana-farm.png "https://apac.acana.com/dw/image/v2/BFDW_PRD/on/demandware.static/-/Sites-acana-ap-master-catalog/zh_HK/dw32b168a8/2023/Cat-2023/WILD%20PRAIRIE/ACANA%20Cat%20Highest%20Protein%20Wild%20Prairie%20Front%20Right%204.5Kg%20APAC.png?sw=450"

# 2004 - 百利经典无谷鸡肉 (Instinct Original Chicken) - 官网
echo "[4/23] 百利经典无谷..."
curl -sL -o food-instinct-chicken.png "https://instinctpetfood.com.sg/wp-content/uploads/2023/05/Instinct-OG-cat-chicken-11lb-1.png" -H "User-Agent: Mozilla/5.0"

# 2005 - 纽翠斯黑钻 (Nutrience SubZero) - 官网
echo "[5/23] 纽翠斯黑钻..."
curl -sL -o food-nutrience-blackdiamond.png "https://nutrience.hk/wp-content/uploads/2023/09/SubZero-Cat-FV-1.png" -H "User-Agent: Mozilla/5.0"

# 2006 - GO! 九种肉 (GO Solutions Carnivore) - 官网
echo "[6/23] GO!九种肉..."
curl -sL -o food-go-9meat.png "https://images.ctfassets.net/sa0sroutfts9/1KdnGqKjXR0l5vSIRKNwMT/2cebe32c33d5e0b4cf8b50498e31f67b/go-solutions-carnivore-chicken-turkey-duck-cat-food-background-removed.png?w=450" -H "User-Agent: Mozilla/5.0"

# 2007 - 蓝氏Blue Buffalo Indoor - 官网
echo "[7/23] 蓝氏室内猫粮..."
curl -sL -o food-blue-indoor.png "https://www.bluebuffalo.com/dw/image/v2/BFNL_PRD/on/demandware.static/-/Sites-master-catalog-blue-buffalo/default/dw7afd4b8d/images/product-images/cat/dry-food/indoor-health/800229_BB_Indoor_Chicken_Cat_3lb_Front.png?sw=450" -H "User-Agent: Mozilla/5.0"

# 2008 - 高爷家 (CatGrandpa) - 天猫
echo "[8/23] 高爷家..."
curl -sL -o food-catgrandpa.png "https://img.alicdn.com/imgextra/i3/2216004269830/O1CN01qKHVqZ1h6BKEK7QWG_!!2216004269830-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2009 - 诚实一口 (OneByOne) - 天猫
echo "[9/23] 诚实一口..."
curl -sL -o food-onebyone.png "https://img.alicdn.com/imgextra/i2/2212362879580/O1CN01gJMPj11hLn5xJg2cw_!!2212362879580-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2010 - 网易严选 - 天猫
echo "[10/23] 网易严选..."
curl -sL -o food-netease.png "https://img.alicdn.com/imgextra/i1/2207378882152/O1CN016x3F1U1OuZTG4fYHW_!!2207378882152-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2011 - 阿飞和巴弟 干粮 - 天猫
echo "[11/23] 阿飞和巴弟..."
curl -sL -o food-afbd-dry.png "https://img.alicdn.com/imgextra/i2/2214281520312/O1CN01BSYCZ01ij7pZYxD8i_!!2214281520312-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2012 - 有鱼 YuYu - 天猫
echo "[12/23] 有鱼..."
curl -sL -o food-yuyu.png "https://img.alicdn.com/imgextra/i4/2208591616498/O1CN010HQXPN1SdVhakU7Wy_!!2208591616498-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2013 - 皇家 I27 (Royal Canin Indoor 27) - 官网
echo "[13/23] 皇家I27..."
curl -sL -o food-royal-i27.png "https://cdn.royalcanin-weshare-online.io/wCvhEGsBaxEApS7MihGc/v5/2529p-packshot" -H "User-Agent: Mozilla/5.0"

# 2014 - 渴望幼猫 (Orijen Kitten) - 官网
echo "[14/23] 渴望幼猫..."
curl -sL -o food-orijen-kitten.png "https://apac.orijenpetfoods.com/dw/image/v2/BFDW_PRD/on/demandware.static/-/Sites-orijen-ap-master-catalog/default/dw792c9b1b/ORI%20Cat%20Refresh%202023/Kitten/ORI%20Kitten%20PDP-1.png?sw=450"

# 2015 - 皇家幼猫 K36 (Royal Canin Kitten) - 官网
echo "[15/23] 皇家幼猫K36..."
curl -sL -o food-royal-k36.png "https://cdn.royalcanin-weshare-online.io/xcvhEGsBaxEApS7MjBGz/v3/2522p-packshot" -H "User-Agent: Mozilla/5.0"

# 2016 - 巅峰马鲛鱼羊肉罐 (Ziwi Peak Mackerel Lamb) - Shopify CDN
echo "[16/23] 巅峰马鲛鱼羊肉罐..."
curl -sL -o food-ziwi-mackerel.png "https://cdn.shopify.com/s/files/1/0727/9850/3211/files/ZIWI_PeakWetMackerelLambCat_185g_Front.png?v=1696406284&width=450" -H "User-Agent: Mozilla/5.0"

# 2017 - 巅峰鸡肉罐 (Ziwi Peak Chicken) - Shopify CDN
echo "[17/23] 巅峰鸡肉罐..."
curl -sL -o food-ziwi-chicken.png "https://cdn.shopify.com/s/files/1/0727/9850/3211/files/ZIWI_PeakWetFreeFarmChicken_185g_Front.png?v=1696404870&width=450" -H "User-Agent: Mozilla/5.0"

# 2018 - K9 Natural 鸡肉罐 - 官网
echo "[18/23] K9 Natural鸡肉罐..."
curl -sL -o food-k9-chicken.png "https://cdn.shopify.com/s/files/1/0376/5892/4180/products/K9Natural_Cat_Can_ChickenFeast_170g_Front.png?v=1680563847&width=450" -H "User-Agent: Mozilla/5.0"

# 2019 - 小李子罐头 - 天猫
echo "[19/23] 小李子罐头..."
curl -sL -o food-xiaolizi.png "https://img.alicdn.com/imgextra/i4/2206854065498/O1CN01fhI7Le1RBv6KlXQXr_!!2206854065498-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2020 - 朗诺冻干 - 天猫
echo "[20/23] 朗诺冻干..."
curl -sL -o food-langnuo-chicken.png "https://img.alicdn.com/imgextra/i3/2206686532426/O1CN01X9o9Qn1T3YQYmZsDl_!!2206686532426-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2021 - 阿飞和巴弟 冻干 - 天猫
echo "[21/23] 阿飞和巴弟冻干..."
curl -sL -o food-afbd-fd.png "https://img.alicdn.com/imgextra/i1/2214281520312/O1CN01U7hs331ij7pbwLkWr_!!2214281520312-0-cib.jpg" -H "User-Agent: Mozilla/5.0"

# 2022 - CIAO猫条 - 官网
echo "[22/23] CIAO猫条..."
curl -sL -o food-ciao-churu.png "https://www.inaba-ciao.com/wp-content/uploads/2025/11/SC-71.png" -H "User-Agent: Mozilla/5.0"

# 2023 - 伟嘉妙鲜包 (Whiskas) - 官网
echo "[23/23] 伟嘉妙鲜包..."
curl -sL -o food-whiskas.png "https://cdn-prod.medicaldialogues.in/h-upload/2023/08/02/189131-whiskas.webp" -H "User-Agent: Mozilla/5.0"

echo ""
echo "=== 下载完成，检查文件大小 ==="
for f in food-*.png; do
    size=$(stat -f%z "$f" 2>/dev/null || stat -c%s "$f" 2>/dev/null)
    if [ "$size" -lt 1000 ]; then
        echo "⚠️  $f - ${size}B (可能下载失败)"
    else
        echo "✅ $f - $(($size/1024))KB"
    fi
done

echo ""
echo "=== 复制猫粮图作为PGC测评首图 ==="
cp food-orijen-6fish.png pgc-orijen-6fish.png
cp food-orijen-chicken.png pgc-orijen-chicken.png
cp food-acana-farm.png pgc-acana-farm.png
cp food-instinct-chicken.png pgc-instinct-chicken.png
cp food-nutrience-blackdiamond.png pgc-nutrience-blackdiamond.png
cp food-catgrandpa.png pgc-catgrandpa.png
cp food-onebyone.png pgc-onebyone.png
cp food-netease.png pgc-netease.png
cp food-royal-i27.png pgc-royal-i27.png
cp food-ziwi-mackerel.png pgc-ziwi-mackerel.png
cp food-k9-chicken.png pgc-k9-chicken.png
cp food-langnuo-chicken.png pgc-langnuo-chicken.png
cp food-ciao-churu.png pgc-ciao-churu.png
echo "PGC首图复制完成"
