<template>
    <div class="container">
        <form enctype="multipart/form">
            <div class="dropbox">
                <input
                    name="Browse files"
                    class="input-file"
                    multiple
                    type="file"
                    ref="files"
                    @change="selectFile"
                    @drop="dropFile"
                />
                <div class ="dropbox-header">
                    <p class="call-to-action">Drag and drop file(s) here or click to pick them</p>
                    <p class="description">{{this.description}}</p>
                </div>
                <v-row
                    align="center"
                >
                    <v-col
                        align-self="auto"
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
                <p class="error-message" v-if="this.excluded && this.excludedFiles.length === 1">This file was not accepted: {{this.excludedFiles}}</p>
                <p class="error-message" v-if="this.excluded && this.excludedFiles.length > 1">These files were not accepted: {{this.excludedFiles}}</p>
            </div>
            <v-row justify="center">
                <v-btn
                color="#7dcdbe"
                @click="uploadFiles"
                >Add submission file(s)</v-btn>
                <v-btn
                @click="removeAllFiles"
                color="#f07387"
                >Remove all submission files</v-btn>
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
            },

        },
        // created(){
        //     this.getAllowedFilePatterns();
        // },
        data(){
            return {
                files: [],
                excludedFiles: [],
                error: false,
                excluded: false,
                description: "",
            }
        },
        props: {
            submission: Object,
            filePatterns: String,
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
                this.excluded = false;
                this.excludedFiles = [];
            },
            uploadFiles(){
                this.excludedFiles = [];
                this.excluded = false;
                if (!this.submission) {
                    this.$emit('add');
                }
                let i;
                for(i = 0; i < this.files.length; i++){
                    this.submitFile(i)
                }
            },
            fileNotAllowed(){
                this.excluded = true;
            },
            submitFile(i){
                console.log(this.files[i].name)
                api.addSubmissionFile(this.files[i].name, this.submission.id, this.dbId, this.taskId, this.tabId).then(response => {
                    if(response.data.excludedFiles.length > 0){
                        this.excludedFiles = [...this.excludedFiles, ...response.data.excludedFiles];
                        this.fileNotAllowed();
                    }
                    console.log(response)
                    this.sendSubmission(i);
                })
            },
            sendSubmission(i){
                api.fileSubmission(this.files[i], this.submission.id, this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response);
                })
                this.$emit('refresh');
            },
            // getAllowedFilePatterns(){
            //     api.getSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
            //         if(response.data.patternDescriptions === ""){
            //             this.description = "Allowed file patterns: *.*";
            //         }
            //         else{
            //             this.description = "Allowed file patterns: " + response.data.patternDescriptions;
            //         }
            //     }).catch(() => {});
            // },
            removeAllFiles(){
                this.files = [];
                this.excluded = false;
                this.excludedFiles = [];
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
        border: 10px solid #F3E5FF;
        margin-bottom: 10px
    }
    .input-file {
        opacity: 0;
        width: 100%;
        height: 100%;
        position: absolute;
    }
    .dropbox:hover {
        background: #E0E0E0;
    }
    .dropbox .call-to-action{
        color: #32005C;
        font-size: 1.5rem;
        text-align: center;
    }
    .dropbox .error-message{
        color: red;
        font-size: 1rem;
        text-align: center;
        border: 5px solid red;
        padding: 10px 0;
    }
    .dropbox-header{
        padding: 75px 0;
    }
    .description {
        color: #32005C;
        font-size: 1.2rem;
        text-align: center;
        opacity: 0.6;
    }
</style>
