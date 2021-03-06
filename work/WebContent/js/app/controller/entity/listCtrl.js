/**
 *         enableSorting: true,
 columnDefs: [
            {name: 'firstName', field: 'first-name'},
            {name: '1stFriend', field: 'friends[0]'},
            {name: 'city', field: 'address.city'},
            {name: 'getZip', field: 'getZip()', enableCellEdit: false}
        ],
        data: [{
            "first-name": "Cox",
            "friends": ["friend0"],
            "address": {street: "301 Dove Ave", city: "Laurel", zip: "39565"},
            "getZip": function () {
                return this.address.zip;
            }
        }
        ]
 */
var listCtrl = function ($scope, $http, $location, $modal, loginService, i18nService, $routeParams) {
    "use strict";
    //i18nService.setCurrentLang('zh-cn');
    $scope.entityType = $routeParams.entityType;
    $scope.captalizedEntityType = $scope.entityType.charAt(0).toUpperCase() + $scope.entityType.substr(1);
    $scope.gridOptions = {
        enableSorting: true,
        enableColumnResizing: true,
        enableGridMenu: true,
        showColumnFooter: true,
        enablePaginationControls: true,
        enableHorizontalScrollbar: 0,
        enableVerticalScrollbar: 0,
        paginationPageSizes : [10, 20, 40, 80],
        paginationPageSize : 18
    };

    $scope.refreshData = function() {
        $http.get("/rest/entity/" + $scope.captalizedEntityType, {}).success(function (metaData) {
            $scope.gridOptions.columnDefs = metaData.result;
            $scope.gridOptions.columnDefs.push({
                name: "operation", displayName: "操作", enableCellEdit: false, width: 100,
                cellTemplate: '<div class="ui-grid-cell-contents list-operation-cell">' +
                '<a href="/' + $scope.entityType + '/edit/{{row.entity.id}}" class="glyphicon glyphicon-edit"></a>' +
                '<span class="list-click-icon"><a ng-click="grid.appScope.deActive(row)"  href="#" class="glyphicon glyphicon-remove"></a></span>' +
                '<span class="list-click-icon"><a href="/' + $scope.entityType + '/detail/{{row.entity.id}}" class="glyphicon glyphicon-eye-open"></a></span>' +
                '</div>'
            });
            $http.get("/rest/" + $scope.captalizedEntityType, {}).success(function (entityData) {
                $scope.gridOptions.data = entityData.result;
            })
        });
    };

    $scope.refreshData();

    $scope.deActive = function (row) {
        var modalTitle;
        if (row.entity.name) {
            modalTitle = "确认删除名为 " + row.entity.name + " 的 " + $scope.captalizedEntityType + " 数据？"
        } else {
            modalTitle = "确认删除编号为 " + row.entity.id + " 的 " + $scope.captalizedEntityType + " 数据？"
        }
        var modalInstance = $modal.open({
            templateUrl: '/templates/modal.html',
            controller: modalInstanceCtrl,
            resolve: {
                modalData: function () {
                    return {
                        title: modalTitle,
                        body: "数据将会被从用户界面上删除，并不再包含于相关报表中。",
                        yesLabel: "确定",
                        noLabel: "取消"
                    };
                }
            }
        });
        modalInstance.result.then(function () {
            var id = row.entity.id;
            var entity = {
                'id': id, 'active': false
            };
            var index = $scope.gridOptions.data.indexOf(row.entity);
            $http.put("/rest/" + $scope.captalizedEntityType + "/" + id, {
                'entity': entity
            }, {}).success(function () {
                $scope.gridOptions.data.splice(index, 1);
            }).error(function () {
                console.info('Error to delete data:[' + row + ']');
            });
        }, function () {
            console.debug('Modal dismissed at: ' + new Date());
        });
    }
};

listCtrl.$inject = ['$scope', '$http', '$location', '$modal', 'loginService', 'i18nService', '$routeParams'];

angular.module('mainApp').controller('listCtrl', listCtrl);
