app.controller("itemCatController", function ($scope, $controller, itemCatService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        itemCatService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;
        if($scope.entity.id != null){//更新
            object = itemCatService.update($scope.entity);
        } else {//新增
            object = itemCatService.add($scope.entity);
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
        itemCatService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            itemCatService.delete($scope.selectedIds).success(function (response) {
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
        itemCatService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //根据父分类id查询其子分类
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.list = response;
        });
    };

    $scope.grade = 1;//默认1级
    $scope.selectList = function (grade, entity) {
        $scope.grade = grade;

        switch (grade){
            case 1:
                $scope.entity_1 = null;
                $scope.entity_2 = null;
                break;
            case 2:
                $scope.entity_1 = entity;
                $scope.entity_2 = null;
                break;
            default:
                $scope.entity_2 = entity;
        }

        $scope.findByParentId(entity.id);
    }

});