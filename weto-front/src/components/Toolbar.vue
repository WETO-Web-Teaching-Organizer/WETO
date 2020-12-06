<template>
  <nav>
    <v-app-bar app dark dense clipped-left class="primary">
      <v-app-bar-nav-icon @click="sidebar = !sidebar"></v-app-bar-nav-icon>
      <v-toolbar-title>WETO</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn text>
        <v-icon>face</v-icon>
        {{ userName }}
      </v-btn>
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
          <v-list-item to="/courses">
            <v-list-item-icon>
              <v-icon>home</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                Home
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

      <v-treeview :items="subTasks" hoverable>
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
        sidebar: null,
      }
    },
    computed: {
      courseName() {
        return this.$store.getters.selectedCourse.name;
      },
      subTasks() {
        return this.$store.getters.subTaskTree;
      },
      userName() {
        return this.$store.getters.user.firstNameData.value + ' ' + this.$store.getters.user.lastNameData.value;
      }
    },
    methods: {
      courseSelected() {
        return this.$store.getters.selectedCourse.courseTaskId !== null &&
          this.$store.getters.selectedCourse.databaseId !== null;
      },
      selectRootTask() {
        this.$store.commit("setTask", this.$store.getters.selectedCourse);
      },
      selectSubTask(item) {
        this.$store.commit("setTask", item);
        this.$router.replace('/task');
      },
      itemName(item) {
        return item.name.replace(/&Auml;/g, 'Ä').replace(/&auml;/g, 'ä').replace(/&Ouml;/g, 'Ö').replace(/&ouml;/g, 'ö');
      }
    }
  }
</script>
