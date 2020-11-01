<template>
    <div class="container">
        <form enctype="multipart/form">
            <div class="dropbox">
                <input
                    class="input-file"
                    multiple
                    type="file"
                    ref="files"
                    @change="selectFile"
                    @drop="dropFile"
                />
                <p class="call-to-action">{{this.description}}</p>
                <v-row>
                    <v-col
                        v-for="(file, i) in files"
                        :key="file.name"
                    >
                        <v-chip
                        close
                        @click:close="files.splice(i,1)">
                            <strong>{{file.name}}</strong>
                        </v-chip>
                    </v-col>
                </v-row>
            </div>
            <v-row justify="center">
                <v-btn
                color="#7dcdbe"
                @click="submitFiles"
                >Complete submission(s)</v-btn>
                <v-btn
                @click="removeAllFiles"
                color="#f07387"
                >Cancel submission(s)</v-btn>
            </v-row>
        </form>
    </div>
</template>
<script>
    import api from '../backend-api'
    export default {
        name: 'filesubmit',
        computed: {
            status() {
                return this.$store.getters.status;
            },
            taskId() {
                return this.$store.getters.currentTask;
            },
            dbId() {
                return this.$store.getters.selectedCourse.databaseId;
            },
            tabId() {
                return 4;
            },
            taskName() {
                return this.$store.getters.selectedCourse.name;
            },
            user() {
                return this.$store.getters.user;
            },
            subTasks() {
                return this.$store.getters.subTasks;
            }
        },
        created(){
            this.getSubmissionId();
        },
        data(){
            return {
                submissionId: "",
                files: [],
                error: false,
            }
        },
        methods:{
            dropFile(event){
                event.preventDefault();
                this.$refs.files.files = event.dataTransfer.files;
                this.selectFile();
            },
            selectFile(){
                const files = this.$refs.files.files;
                this.files = [...this.files, ...files];
                console.log(this.files)
            },
            submitFiles(){

                api.addSubmissionFile(this.files[0].name, this.submissionId, this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response);
                    this.sendSubmission();
                })
            },
            sendSubmission(){
                api.fileSubmission(this.files[0], this.submissionId, this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response)
                })
            },
            validate(file){
                return file
            },
            getSubmissionId(){
                let submissionId;
                api.getSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response)
                    submissionId = response.data.submissions[0].id;
                    this.submissionId = submissionId;
                });
            },
            removeAllFiles(){
                console.log(this.files);
                this.files = [];
                console.log(this.files);
            }
        },
        props:{
            fileName: {
                type: Array,
                required: true,
                default: function () {return []},
            },
            fileType: {
                type: Array,
                required: true,
                default: function () {return []},
            },
            fileLim:{
                type: Number,
                required: true,
                default: 1,
            },
            description: {
                type: String,
                required: true,
                default: "Drag and drop file(s) here or click to pick them",
            }
        }
    }
</script>

<style scoped>
    .container {
       margin-bottom: 20px; 
    }
    .dropbox {
        min-height: 200px;
        padding: 10px 10px;
        position: relative;
        cursor: pointer;
        box-shadow: 0 0 10px lightgrey;
        background: white;
        border: 5px solid #F3E5FF;
        margin-bottom: 10px
    }
    .input-file {
        opacity: 0;
        width: 100%;
        height: 200px;
        position: absolute;
    }
    .dropbox:hover {
        background: lightblue;
    }
    .dropbox .call-to-action{
        color: #32005C;
        font-size: 1.5rem;
        text-align: center;
        padding: 70px 0;
    }
</style>
