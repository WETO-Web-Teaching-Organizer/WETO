<template>
  <nav>
    <v-app-bar app dark dense class="primary">
      <v-app-bar-nav-icon @click="sidebar = !sidebar" dark/>
      <v-divider vertical/>
      <v-toolbar-items v-if="courseSelected()">
        <v-btn text class="hidden-sm-and-down" router to="/task">
          <v-icon left>home</v-icon>
          <span>MAIN</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/grading">
          <v-icon left>grade</v-icon>
          <span>GRADING</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/submissions">
          <v-icon left>subject</v-icon>
          <span>SUBMISSIONS</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/forum">
          <v-icon left>forum</v-icon>
          <span>FORUM</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/groups">
          <v-icon left>group</v-icon>
          <span>GROUPS</span></v-btn>
        
        <v-btn text fab class="hidden-md-and-up" router to="/task">
          <v-icon>home</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/grading">
          <v-icon>grade</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/submissions">
          <v-icon>subject</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/forum">
          <v-icon>forum</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/groups">
          <v-icon>group</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>
    
    <v-navigation-drawer
        app
        light
        floating
        class="info"
        v-model=sidebar>
      <v-app-bar dark dense class="primary">
        <v-btn text block class="font-weight-bold text-justify" router to="/">WETO</v-btn>
      </v-app-bar>
      
      <v-btn block text class="secondary">
        <v-icon>face</v-icon>
        <span>
          {{ this.$store.getters.user.firstNameData.value }}
          {{ this.$store.getters.user.lastNameData.value }}
        </span>
      </v-btn>
      
      <v-divider/>
      
      <v-btn block text v-if="courseName !== ''" @click="selectRootTask()">{{ courseName }}</v-btn>
      
      <v-treeview rounded hoverable :items="subTasks" @click="testiFunktio()">
        <template slot="label" slot-scope="{ item }">
          <a @click="selectSubTask(item)">{{ item.name }}</a>
        </template>
      </v-treeview>
      
      <v-list rounded class="pa-0" dense v-if="courseSelected()">
        <v-list-item v-for="link in links" :key="link.text" router :to="link.route">
          <v-list-item-content>
            <span>
              {{ link.text }}
            </span>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
  </nav>
</template>

<script>

  export default {
    name: "Toolbar.vue",
    data() {
      return {
        sidebar: true,
        links: [
          {icon: 'exit_to_app', text: 'Return to course list', route: '/', toolbar: false}
        ]
      }
    },
    computed: {
      courseName: function () {
        return this.$store.getters.selectedCourse.name;
      },
      subTasks: function () {
        return this.$store.getters.subTaskTree;
      }
    },
    methods: {
      courseSelected() {
        return this.$store.getters.selectedCourse.courseTaskId !== null &&
          this.$store.getters.selectedCourse.databaseId !== null;
      },
      selectRootTask() {
        this.$store.commit("setTask", this.$store.getters.selectedCourse.courseTaskId);
      },
      selectSubTask(item) {
        this.$store.commit("setTask", item.id);
      }
    }
  }
</script>