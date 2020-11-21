<template>
    <div>
        <v-data-table
            :headers="gradeHeaders"
            :items="grades"
            :expanded.sync="expanded"
            item-key="name"
            show-expand
            class="table-header"
            disable-sort
            hide-default-footer
        >
            <template v-slot:top class="table-header">
                <v-toolbar flat>
                    <v-toolbar-title>Grade</v-toolbar-title>
                </v-toolbar>
            </template>
            <template v-slot:item.rating="{ item }">
                <v-chip
                    :color="getColor(item.rating)"
                >
                    {{ item.rating }} {{item.validity}}
                </v-chip>
            </template>
            <template v-slot:expanded-item="{headers, item}">
                <td :colspan="3">
                    <p>Submitted files:</p>
                    <v-simple-table>
                        <template v-slot default>
                            <thead>
                                <th class="text-left">File</th>
                                <th class="text-left">Size</th>
                                <th class="text-left">Actions</th>
                            </thead>
                            <tbody>
                                <tr v-for="file in item.files" :key="file.id">
                                    <td>{{file.name}}</td>
                                    <td>{{file.size}}</td>
                                    <td>
                                        <v-btn depressed class="docAction" color="primary" @click="downloadSubmissionFile(doc.fileName, doc.id)">
                                            <v-icon>download</v-icon>
                                            Download
                                        </v-btn>
                                    </td>
                                </tr>
                            </tbody>
                        </template>
                    </v-simple-table>
                </td>
            </template>
        </v-data-table>
    </div>
</template>

<script>
    import api from '../backend-api'

    export default{
        name: 'taskGrade',
        created(){
            this.getNodeGrades();
            this.getStudentLeafGrades();
            this.getGrade();
        },
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
                return 3;
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
        data(){
            return{
                full: 3.0,
                valid: 2.0,
                notValid: 0.0,
                grades: [
                    {
                        name: 'Ope1',
                        time: '18.11.2019 13:02',
                        rating: 3.0,
                        validity: "Valid",
                        files: [
                            {
                                id: 0,
                                name: 'Hello_world.txt',
                                size: '245'
                            }
                        ]
                    },
                    {
                        name: 'Ope2',
                        time: '18.11.2019 13:02',
                        rating: 2.0,
                    },
                    {
                        name: 'Ope3',
                        time: '18.11.2019 13:02',
                        rating: 0.0,
                    },
                    {
                        name: 'Ope4',
                        time: '18.11.2019 13:02',
                        rating: 1.0,
                    }
                ],
                gradeHeaders: [
                    {
                        text: 'Reviewer',
                        align: 'start',
                        sortable: false,
                        value: 'name',
                    },
                    { text: 'Time', sortable: false, value: 'time' },
                    { text: 'Rating', sortable: false, value: 'rating' },
                    { text: '', sortable: false, value: 'data-table-expand' },
                ],
            }
        },
        methods: {
            getNodeGrades(){
                api.getJSONNodeGrades(this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response);
                });
            },
            getStudentLeafGrades(){
                api.getJSONStudentLeafGrades(this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response);
                });
            },
            getGrade(){
                api.getJSONGrade(this.dbId, this.taskId, this.tabId).then(response => {
                    console.log(response);
                });

            },
            downloadSubmissionFile(filename, id) {
                api.downloadSubmissionFile(filename, id, this.dbId, this.taskId, this.tabId);
            },
            getColor(rating) {
                if(rating === this.full) return 'green'
                else if(rating >= this.valid) return 'blue'
                else if (rating < this.valid && rating > this.notValid) return 'yellow'
                else return 'red'
            }
        }
    }
</script>

<style scoped>
    thead {
        background-color: #f3e5ff;
    }
    .v-data-table-header{
        background-color: #f3e5ff;
    }
    .docAction {
        margin-right: 2em;
        margin-top: 0.5em;
        padding-left: 12px;
    }
</style>
