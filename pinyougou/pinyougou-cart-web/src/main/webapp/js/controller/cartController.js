app.controller("cartController", function ($scope, cartService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        })

    };

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总价和总数量
            $scope.totalValue = cartService.sumTotalValue(response);
        });

    };

    //增减购物车商品购买数量
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId, num).success(function (response) {
            if (response.success) {
                //操作成功，刷新列表
                $scope.findCartList();
            } else {
                alert(response.message);
            }

        });
    };

});