app.controller("baseController",function ($scope) {

    //初始化分页参数
    $scope.paginationConf={
        currentPage:1,//当前页
        totalItems:10,//总记录e数
        itemsPerPage:10,//每页大小
        perPageOptions:[10,20,30,40,50],//可选择的每页大小
        onChange:function () {//当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };

    //加载表格数据
    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    //定义一个放置选择了id的数组
    $scope.selectedIds = [];

    $scope.updateChecked = function ($event, id) {
        //判断是否选中
        if($event.target.checked){
            $scope.selectedIds.push(id);
        }else {
            var index = $scope.selectedIds.indexOf(id);
            //删除位置，删除个数
            $scope.selectedIds.splice(index,1);
        }
    };

});