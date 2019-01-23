
//controller层
app.controller("brandController",function ($scope,$controller,brandService) {
    //继承baseController
    $controller("baseController",{$scope:$scope});

    //分页查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(function (response) {
            //更新记录列表
            $scope.list = response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        }).error(function () {
            alert("加载数据失败!");
        });
    };

    //保存
    $scope.save = function () {
        var obj;
        if($scope.entity.id != null ){
            obj = brandService.update($scope.entity);
        }else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if(response.success){
                //重新加载列表
                $scope.reloadList();
                alert(response.message);
            }else {
                alert(response.message);
            }
        })
    };

    //根据id主键查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };


    //批量删除
    $scope.delete = function () {
        if($scope.selectedIds.length<1){
            alert("请选择要删除的记录");
            return;
        }

        if(confirm("确定要删除选中的记录吗？")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds = [];
                    alert(response.message);
                }else {
                    alert(response.message);
                }
            })
        }
    };

    //搜索
    //定一个空的搜索条件对象
    $scope.searchEntity = {};

    $scope.search = function (page, rows) {
        brandService.search($scope.searchEntity,page,rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }
});