<template>
  <nav :style="optimizeLargeDisplay.font">
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
        :style="optimizeLargeDisplay.nav"
    >
      <v-list nav :style="{padding: '0.5em'}">
        <v-list-item-group>
          <v-list-item to="/courses" :style="{padding: '0 0.5em', margin: '0 0 0.5em'}">
            <v-list-item-icon>
              <v-icon :style="optimizeLargeDisplay.icon">home</v-icon>
            </v-list-item-icon>
            <v-list-item-content :style="{padding: '0.75em 0'}">
              <h3>
                Home
              </h3>
            </v-list-item-content>
          </v-list-item>

          <v-list-item v-if="courseName !== ''" @click="selectRootTask" :style="{padding: '0 0.5em'}">
            <v-list-item-icon>
              <v-icon :style="optimizeLargeDisplay.icon">info</v-icon>
            </v-list-item-icon>
            <v-list-item-content :style="{padding: '0.75em 0'}">
              <h3>{{ courseName }}</h3>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>

      <v-treeview :items="subTasks" hoverable open-on-click>
        <template slot="label" slot-scope="{ item }">
          <a @click="selectSubTask(item)" elevation="2">{{ itemName(item) }}</a>
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
      },
      optimizeLargeDisplay() {
        if (this.$vuetify.breakpoint.width >= 2560) {
          return { font: {fontSize: '2em', lineHeight: '3'}, icon: {fontSize: '48px'}, nav: {width: '16em'} }
        } else {
          return { font: {fontSize: '1em', lineHeight: '1.5'}, icon: {fontSize: '24px'}, nav: {width: '16em'} }
        }
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