/**
 * Utility for handling file downloads from Axios responses.
 */

/**
 * Parses the Content-Disposition header and triggers a browser download.
 *
 * @param {import('axios').AxiosResponse} response - The Axios response object containing the blob
 * @param {string} fallbackFilename - Filename to use if header is missing
 * @param {string} [mimeType='text/csv'] - The MIME type for the blob
 */
export function triggerDownload(response, fallbackFilename, mimeType = 'text/csv') {
  let filename = fallbackFilename
  const disposition = response.headers['content-disposition']
  
  if (disposition && disposition.includes('attachment')) {
    const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(disposition)
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '')
    }
  }

  const url = URL.createObjectURL(new Blob([response.data], { type: mimeType }))
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  URL.revokeObjectURL(url)
}
