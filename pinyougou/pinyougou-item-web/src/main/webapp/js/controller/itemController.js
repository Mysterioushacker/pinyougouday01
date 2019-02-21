app.controller("itemController", function ($scope, $http) {

    //加减购买数量
    $scope.num = 1;
    $scope.addNum = function (num) {
        $scope.num = $scope.num + num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    //记录选择了的规格
    $scope.specificationItems = {};
    //选择规格
    $scope.selectSpecification = function (specName, value) {
        $scope.specificationItems[specName] = value;

        //获取sku
        searchSku();
    };
    //判断是否是选中了的规格
    $scope.isSelected = function (specName, value) {
        if ($scope.specificationItems[specName] == value) {
            return true;
        }
        return false;
    };

    //加载默认sku
    $scope.loadSku = function () {
        $scope.sku = skuList[0];//默认第一个SKU商品

        //设置当前选择的规格
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    };

    //比较两个对象是否一致
    matchObject = function (map1, map2) {
        for (var j in map1) {
            if (map1[j] != map2[j]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        return true;
    };

    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            var obj = skuList[i];
            if (matchObject($scope.specificationItems, skuList[i].spec)) {
                $scope.sku = skuList[i];
                return;
            }
        }

        //如果没有找到匹配的，则默认如下：
        $scope.sku = {"id": 0, "title": "-----", "price": 0};
    };

    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addItemToCartList.do?itemId="
            + $scope.sku.id + "&num=" + $scope.num,{"withCredentials":true})
            .success(function (response) {
                if (response.success) {
                    location.href = "http://cart.pinyougou.com";
                } else {
                    alert(response.message);
                }
            });
    };
});