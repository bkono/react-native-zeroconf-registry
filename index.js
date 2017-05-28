
import { NativeModules, DeviceEventEmitter } from 'react-native'
import { EventEmitter } from 'events'

const { RNZeroconfRegistry } = NativeModules;

console.log("RNZeroconfRegistry = " + RNZeroconfRegistry);

// export default RNZeroconfRegistry;
export default class ZeroconfRegistry extends EventEmitter {
  constructor(props) {
    console.log("in ZeroconfRegistry constructor")
    super(props)

    this._services = {}
    this._listeners = {}

    this.addRegistryListeners()
  }

  addRegistryListeners() {
    if (Object.keys(this._listeners).length) {
      return this.emit('error', new Error('RNZeroconf listeners already in place.'))
    }

    this._listeners.start = DeviceEventEmitter.addListener('RNZeroconfStart', () => this.emit('start'))
    this._listeners.stop = DeviceEventEmitter.addListener('RNZeroconfStop', () => this.emit('stop'))
    this._listeners.error = DeviceEventEmitter.addListener('RNZeroconfError', err => this.emit('error', err))

    this._listeners.found = DeviceEventEmitter.addListener('RNZeroconfFound', service => {
      if (!service || !service.name) { return }
      const { name } = service

      this._services[name] = service
      this.emit('found', name)
      this.emit('update')
    })

    this._listeners.remove = DeviceEventEmitter.addListener('RNZeroconfRemove', service => {
      if (!service || !service.name) { return }
      const { name } = service

      delete this._services[name]

      this.emit('remove', name)
      this.emit('update')
    })

    this._listeners.resolved = DeviceEventEmitter.addListener('RNZeroconfResolved', service => {
      if (!service || !service.name) { return }

      this._services[service.name] = service
      this.emit('resolved', service)
      this.emit('update')
    })
  }

  /**
   * Remove all event listeners and clean map
   */
  removeDeviceListeners() {
    Object.keys(this._listeners).forEach(name => this._listeners[name].remove())
    this._listeners = {}
  }

  /**
   * Get all the services already resolved
   */
  getServices() {
    return this._services
  }

  /**
   * Scan for go-micro service by name,
   */
  scan(serviceName) {
    console.log("scan called with serviceName = " + serviceName)
    this._services = {}
    this.emit('update')
    RNZeroconfRegistry.scan(serviceName)
  }

  // /**
  //  * Scan for services across a given namespace
  //  * @param {*} namespace 
  //  */
  // scanNamespace(namespace) {
  //   this._service = {}
  //   this.emit('update')
  //   RNZeroconfRegistry.scanNamespace(namespace)
  // }

  /**
   * Stop current scan if any
   */
  stop() {
    RNZeroconfRegistry.stop()
  }
}
