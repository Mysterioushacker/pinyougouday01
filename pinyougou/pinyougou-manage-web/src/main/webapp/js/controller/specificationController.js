app.controller("specificationController", function ($scope, $controller, specificationService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        specificationService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        specificationService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;
        if($scope.entity.specification.id != null){//更新
            object = specificationService.update($scope.entity);
        } else {//新增
            object = specificationService.add($scope.entity);
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
        specificationService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            specificationService.delete($scope.selectedIds).success(function (response) {
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
        specificationService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //新增规格选项
    $scope.addTableRow = function () {
        $scope.entity.specificationOptionList.push({});
    };

    //删除规格选项
    $scope.deleteTableRow = function (index) {
        $scope.entity.specificationOptionList.splice(index, 1);
    };

});