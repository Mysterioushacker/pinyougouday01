app.controller("sellerController", function ($scope, $controller, sellerService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        sellerService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        sellerService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;
        if($scope.entity.id != null){//更新
            object = sellerService.update($scope.entity);
        } else {//新增
            object = sellerService.add($scope.entity);
        }
        object.success(function (response) {
            if(response.success){
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };

    $scope.findOne = function (id) {
        sellerService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            sellerService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };

    $scope.searchEntity = {};//初始为空
    $scope.search = function (page, rows) {
        sellerService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //新增商家信息
    $scope.add = function () {
        sellerService.add($scope.entity).success(function (response) {
            if(response.success){
                //注册成功，跳转到登录页面
                location.href = "shoplogin.html";
            } else {
                alert(response.message);
            }
        });
    };

    //修改商家状态
    $scope.updateStatus = function (sellerId, status) {
        var updateData = {"sellerId": sellerId, "status": status};
        sellerService.update(updateData).success(function (response) {
            if(response.success){
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };

});