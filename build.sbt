import org.scalajs.linker.interface.ModuleSplitStyle

lazy val monitor = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "2.13.10",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("monitor")))
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo" %%% "laminar"        % "15.0.1",
      "io.circe" %%% "circe-core"      % "0.14.5",
      "io.circe" %%% "circe-parser"    % "0.14.5",
      "io.circe" %%% "circe-generic"   % "0.14.5",
    ),
  )
