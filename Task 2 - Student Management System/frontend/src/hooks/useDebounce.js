import { useEffect, useState } from 'react'

/**
 * useDebounce — delays propagating a rapidly changing value until the user
 * stops typing.  Used by SearchBar to avoid sending a request on every keystroke.
 *
 * @param {*} value - the value to debounce
 * @param {number} delayMs - milliseconds to wait (default: 300ms)
 * @returns the debounced value
 */
function useDebounce(value, delayMs = 300) {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delayMs)
    return () => clearTimeout(timer)
  }, [value, delayMs])

  return debouncedValue
}

export default useDebounce
