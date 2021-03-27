<template>
    <div class="navigation">
        <ul class="breadcrumb">

            <li v-for="header in headers">
                <span v-if="header.url">
                    <a :href="header.url">{{header.label}}</a>
                </span>

                <span v-else class="current">{{header.label}}</span>
            </li>

        </ul>
    </div>
</template>

<script>
    export default {
        name: "Navigation",
        props:{
            headers:{
                required:true,
                type:Array,
            }
        }
    }
</script>

<style scoped>

</style>