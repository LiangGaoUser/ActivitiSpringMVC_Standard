<template>
    <div class="operation">
        <button type="button" @click="addRecord">添加</button>
        <button type="button" @click="deleteRecord">删除</button>
        <button type="button" @click="checkRecord">审核</button>
        <button type="button" @click="exportList">导出</button>
    </div>
    <div class="list">
        <table id="table-list" class="table">
            <tr>
                <th>
                    <div>
                        <span>#</span>
                    </div>
                </th>
                <th v-for="label in headers.labels">
                    <div>
                        <span>{{label}}</span>
                    </div>
                </th>
                <th>
                    <div>
                        <span>操作</span>
                    </div>
                </th>

            </tr>
            <tr v-for="record in list" :id="record[headers.IDName]" align="center">
                <td>
                    <input type="checkbox" :checked="selectedID.indexOf(record[headers.IDName])>=0" @click="checkItems(record[headers.IDName])"/>
                </td>
                <td v-for="model in headers.model" :title="record[model]">{{record[model]}}</td>
                <td>
                    <button @click="getDetail(record[headers.IDName])" class="btn btn-sm btn-default detail" type="button" title="详细">
                        <span class="glyphicon glyphicon-align-justify"></span>
                    </button>
                    <button @click="editRecord(record[headers.IDName])" class="btn btn-sm btn-default edit" type="button" title="修改">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </td>
            </tr>
        </table>
    </div>
    <list-pagination></list-pagination>
</template>

<script>
    import Bus from '../eventBus'
    import ListPagination from "./ListPagination";


    export default {
        name: "ListTable",
        components: {ListPagination},
        data() {
            return {
                list: [],
                selectedID:[],
            }
        },
        methods: {
            updateListTable: function (conditions, pageIndex) {
                pageIndex = pageIndex || 1;
                conditions = conditions || '';
                axios.post('GetList_vue', {
                    params: {
                        pageIndex,
                        conditions,
                    }
                }).then(function (response) {
                    this.list = response.list
                    this.pageNum = response.pageNum
                })

            },
            exportList:function () {

            },
            addRecord:function () {

            },
            deleteRecord:function () {

            },
            checkRecord:function () {

            },
            getDetail:function () {

            },
            editRecord:function () {

            }
        },
        created() {
            this.updateListTable()
            Bus.$on('updateListTable', function (data) {
                this.updateListTable(data.conditions,data.pageIndex)
            })
        },
        props:{
            headers:{
                required:true,
                type:Object,
            },
            IDName:String,
        }

    }
</script>

<style scoped>

</style>