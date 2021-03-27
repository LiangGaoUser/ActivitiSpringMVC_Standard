<template>
    <div class="pagination">

        <div id="tipbar" :style="{display:(pagenum>0?'none':'')}">
            <span>提示：没有符合条件的记录</span>
        </div>
        <div id="pagebar" :style="{display:(pagenum==0?'none':'')}" style="text-align: right;float: right;">
            <nav aria-span="Page navigation" style="float: right">
                <ul class="pagination pagination-sm">
                    <li style="margin-right: 20px;">
                        <a href="#" style="border: none; background-color: white; margin-right: 20px;">
                            <span>当前页码:[</span>
                            <span id="pageindex">{{pageindex}}</span>
                            <span>]&nbsp;&nbsp;每页记录:[10]&nbsp;&nbsp;总页:[</span>
                            <span id="pagenum">{{pagenum}}</span>
                            <span>]</span></a>
                    </li>
                    <li><a style="text-decoration: none;" id="lnkbtnFirst"
                           @click="getList(1,conditions,pageindex==1)" :class="{'aspNetDisabled':pageindex==1}">首页</a>
                    </li>
                    <li><a style="text-decoration: none;" id="lnkbtnPrevious"
                           @click="getList(pageindex-1,conditions,pageindex==1)"
                           :class="{'aspNetDisabled':pageindex==1}">上一页</a>
                    </li>
                    <li><a style="text-decoration: none;" id="lnkbtnNext"
                           @click="getList(pageindex+1,conditions,pageindex==pagenum)"
                           :class="{'aspNetDisabled':pageindex==pagenum}">下一页</a>
                    </li>
                    <li><a style="text-decoration: none;" id="lnkbtnLast"
                           @click="getList(pagenum,conditions,pagenum==1||pageindex==pagenum)"
                           :class="{'aspNetDisabled':pagenum==1||pageindex==pagenum}">末页</a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
</template>

<script>
    export default {
        name: "ListPagination"
    }
</script>

<style scoped>

</style>