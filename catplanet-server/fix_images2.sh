#!/bin/bash
cd /Users/zhaomu/IdeaProjects/catplanet/catplanet-server/uploads

echo "=== 修复剩余7张猫粮图片 ==="

# 1. 高爷家 - petplus.vip
echo "[1/7] 下载高爷家..."
curl -sL -o food-catgrandpa.png "https://imgs.petplus.vip/315a49c1ec4193ae82bde6cd13c33dbe_1.png"

# 2. 诚实一口 - petplus.vip
echo "[2/7] 下载诚实一口..."
curl -sL -o food-onebyone.png "https://imgs.petplus.vip/b14992fb91de056a787d2587e05ef5d1_1.png"

# 3. 网易严选 - petplus.vip
echo "[3/7] 下载网易严选..."
curl -sL -o food-netease.png "https://imgs.petplus.vip/1595dafc8bc0e6869634f21df690534a_1.png"

# 4. 阿飞和巴弟干粮 E76 - petplus.vip
echo "[4/7] 下载阿飞和巴弟干粮..."
curl -sL -o food-afbd-dry.png "https://imgs.petplus.vip/12d7a499b249f660d76b4d91bcf94a10_1.png"

# 5. 阿飞和巴弟冻干 P86F - petplus.vip
echo "[5/7] 下载阿飞和巴弟冻干..."
curl -sL -o food-afbd-fd.png "https://imgs.petplus.vip/abb4d688bc8fdc3818e338a7527dfe24_1.jpg"

# 6. 有鱼 YOOIU - petrepublic.com.my CDN
echo "[6/7] 下载有鱼..."
curl -sL -o food-yuyu.png "https://cdn1.sgliteasset.com/petrepub/images/product/product-4282921/PA7qiSEQ65f93cb347e3c_1710832819.jpg"

# 7. 巅峰马鲛鱼羊肉罐 - Shopify CDN (peekapaw.com.au)
echo "[7/7] 下载巅峰马鲛鱼..."
curl -sL -o food-ziwi-mackerel.png "https://cdn.shopify.com/s/files/1/0563/6676/0120/files/3_fdd8904d-7aa9-48a5-9cab-66bdf810c532.jpg?v=1710824311&width=2048"

echo ""
echo "=== 检查下载结果 ==="
for f in food-catgrandpa.png food-onebyone.png food-netease.png food-afbd-dry.png food-afbd-fd.png food-yuyu.png food-ziwi-mackerel.png; do
    size=$(wc -c < "$f" 2>/dev/null | tr -d ' ')
    type=$(file -b "$f" 2>/dev/null | head -c 30)
    if [ "$size" -gt 1000 ] 2>/dev/null; then
        echo "✅ $f - ${size}B - $type"
    else
        echo "❌ $f - ${size}B - $type"
    fi
done

echo ""
echo "=== 复制PGC测评首图 ==="
cp food-catgrandpa.png pgc-catgrandpa.png 2>/dev/null && echo "✅ pgc-catgrandpa.png"
cp food-onebyone.png pgc-onebyone.png 2>/dev/null && echo "✅ pgc-onebyone.png"
cp food-netease.png pgc-netease.png 2>/dev/null && echo "✅ pgc-netease.png"

echo ""
echo "=== 全部图片最终状态 ==="
ls -la food-*.png | awk '{printf "%s %s\n", $5, $9}'
