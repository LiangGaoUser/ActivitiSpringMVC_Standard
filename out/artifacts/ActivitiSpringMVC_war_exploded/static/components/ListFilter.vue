<template>
    <div class="filter">
        <el-row v-for="row in items">
            <el-col v-for="col in row" :span="24/row.length">
                <el-col span="6" class="filter-label">{{col.label}}：</el-col>
                <el-col span="18">
                    <el-input v-if="col.type==='plain'" v-model="params[col.model].value" clearable :placeholder="`请输入${col.label}`"></el-input>
                    <el-input v-else-if="col.type==='number'" value="number" v-model="params[col.model].value" clearable :placeholder="`请输入${col.label}`"></el-input>
                    <el-date-picker v-else="col.type==='time'" type="daterange" v-model="params[col.model].value" clearable :placeholder="`请输入${col.label}`"></el-date-picker>
                </el-col>
            </el-col>
        </el-row>

        <el-row type="flex" justify="end">
            <el-col span="8">
                <el-button @click="filterList" type="primary">查询</el-button>
            </el-col>
        </el-row>

    </div>
</template>

<script>
    import Bus from '../eventBus'
    export default {
        name: "ListFilter",
        data(){
            return{
                conditions:'',
                params:{},
            }
        },
        watch:{
          items:function (val) {
              for(row of this.items){
                  for(col of row){
                      this.params[col.model]=col
                  }
              }
          }  
        },
        methods:{
            filterList:function () {
                let params=[]
                for(col of this.params){
                    switch (col.type){
                        case 'plain':
                            if(col.value && col.value.replaceAll(' ','')){
                                col.value=col.value.replaceAll(' ','')
                                params.push(` ${col.model} like '%${col.model}%' `);
                            }
                            break
                        case 'time':
                            if(col.value[0] && col.value[1]){
                                params.push(` ${col.model} between ${col.value[0]} + and ${col.value[1]}`);
                            }
                            break
                        case 'number':
                            if(col.value){
                                let num=Number(col.value.replaceAll(' ',''))
                                if(num){
                                    params.push(` ${col.model} > ${num} `);
                                }
                            }
                            break
                    }
                }
                Bus.$emit('updateListTable',{
                    conditions:params.join(' ')
                });
            }
        },
        props:{
            items:{
                type:Array,
                required:true,
            }
        }

    }
</script>

<style scoped>

</style>