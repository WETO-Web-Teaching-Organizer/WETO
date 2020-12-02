<template>
  <nav>
    <v-app-bar app dark dense clipped-left class="primary">
      <v-app-bar-nav-icon @click="sidebar = !sidebar"></v-app-bar-nav-icon>
      <v-toolbar-title>WETO</v-toolbar-title>
    </v-app-bar>

    <v-navigation-drawer
        app
        light
        clipped
        floating
        class="info"
        v-model="sidebar"
    >
      <v-list nav>
        <v-list-item-group>
          <v-list-item link router to="/courses">
            <v-list-item-icon>
              <v-icon>home</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                Home
              </h3>
            </v-list-item-content>
          </v-list-item>

          <v-list-item link>
            <v-list-item-icon>
              <v-icon>face</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                {{ this.$store.getters.user.firstNameData.value }}
                {{ this.$store.getters.user.lastNameData.value }}
              </h3>
            </v-list-item-content>
          </v-list-item>

          <v-list-item v-if="courseName !== ''" @click="selectRootTask">
            <v-list-item-icon>
              <v-icon>info</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>{{ courseName }}</h3>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>

      <v-treeview :items="subTasks">
        <template slot="label" slot-scope="{ item }">
          <a @click="selectSubTask(item)">{{ itemName(item) }}</a>
        </template>
      </v-treeview>
    </v-navigation-drawer>
  </nav>
</template>

<script>

  export default {
    name: "Toolbar.vue",
    data() {
      return {
        sidebar: true,
      }
    },
    computed: {
      courseName() {
        return this.$store.getters.selectedCourse.name;
      },
      subTasks() {
        return this.$store.getters.subTaskTree;
      },
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
        this.$router.replace('/task');
      },
      itemName(item) {
        return item.name.replace(/&Auml;/g, 'Ä').replace(/&auml;/g, 'ä').replace(/&Ouml;/g, 'Ö').replace(/&ouml;/g, 'ö');
      }
    }
  }
</script>
