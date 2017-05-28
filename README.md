
# react-native-zeroconf-registry

This is a React Native companion plugin for using the [go-zeroconf-registry](https://github.com/bkono/go-zeroconf-registry) in concert with a [go-micro](https://github.com/micro/go-micro) based infrastructure. Everything else in this README is subject to change as the plugin is migrated to be a Registry component, rather than pure zeroconf service discovery.

## Getting started

`$ npm install react-native-zeroconf-registry --save`

### Mostly automatic installation

`$ react-native link react-native-zeroconf-registry`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-zeroconf-registry` and add `RNZeroconfRegistry.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNZeroconfRegistry.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import sh.kono.micro.RNZeroconfRegistryPackage;` to the imports at the top of the file
  - Add `new RNZeroconfRegistryPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-zeroconf-registry'
  	project(':react-native-zeroconf-registry').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-zeroconf-registry/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-zeroconf-registry')
  	```


## Usage
```javascript
import RNZeroconfRegistry from 'react-native-zeroconf-registry';

// TODO: What to do with the module?
RNZeroconfRegistry;
```
  

## Acknowledgements

*Heavily* inspired by [react-native-zeroconf](https://github.com/Apercu/react-native-zeroconf). Initial implementation is mostly drawn from a fork of that excellent repo.
